package com.riskgis.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.riskgis.client.ApihzClient;
import com.riskgis.dto.earthquake.EarthquakeResponse;
import com.riskgis.mapper.EarthquakeRecordMapper;
import com.riskgis.model.EarthquakeRecord;
import com.riskgis.service.EarthquakeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class EarthquakeServiceImpl implements EarthquakeService {

    private static final Logger log = LoggerFactory.getLogger(EarthquakeServiceImpl.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final EarthquakeRecordMapper earthquakeRecordMapper;
    private final ApihzClient apihzClient;

    @Value("${apihz.id}")
    private String apihzId;

    @Value("${apihz.key}")
    private String apihzKey;

    public EarthquakeServiceImpl(EarthquakeRecordMapper earthquakeRecordMapper, ApihzClient apihzClient) {
        this.earthquakeRecordMapper = earthquakeRecordMapper;
        this.apihzClient = apihzClient;
    }

    @Override
    public void syncEarthquakeData() {
        try {
            EarthquakeResponse response = apihzClient.getEarthquake(apihzId, apihzKey);

            if (response == null || response.getData() == null) {
                return;
            }

            List<EarthquakeResponse.EarthquakeItem> dataList = response.getData();
            if (dataList.isEmpty()) {
                return;
            }

            int inserted = 0;
            for (EarthquakeResponse.EarthquakeItem item : dataList) {
                try {
                    EarthquakeRecord record = new EarthquakeRecord();
                    record.setOccurTime(LocalDateTime.parse(item.getAddtime(), FORMATTER));
                    record.setMagnitude(new BigDecimal(item.getLeve()));
                    record.setLatitude(new BigDecimal(item.getWeidu()));
                    record.setLongitude(new BigDecimal(item.getJingdu()));
                    record.setDepth(new BigDecimal(item.getShendu()));
                    record.setLocation(item.getWeizhi());
                    record.setReportTime(LocalDateTime.parse(item.getHctime(), FORMATTER));

                    // 使用 MyBatis-Plus 的 save，利用数据库 UNIQUE 约束避免重复
                    // 先查询是否已存在
                    LambdaQueryWrapper<EarthquakeRecord> wrapper = new LambdaQueryWrapper<>();
                    wrapper.eq(EarthquakeRecord::getOccurTime, record.getOccurTime())
                            .eq(EarthquakeRecord::getLatitude, record.getLatitude())
                            .eq(EarthquakeRecord::getLongitude, record.getLongitude())
                            .eq(EarthquakeRecord::getMagnitude, record.getMagnitude());
                    Long count = earthquakeRecordMapper.selectCount(wrapper);
                    if (count == 0) {
                        earthquakeRecordMapper.insert(record);
                        inserted++;
                    }
                } catch (Exception e) {
                    log.warn("解析地震数据失败: {}", e.getMessage());
                }
            }

            if (inserted > 0) {
                log.info("地震数据同步完成，新增 {} 条记录", inserted);
            }
        } catch (Exception e) {
            log.error("地震数据同步失败: {}", e.getMessage());
        }
    }

    @Override
    public List<EarthquakeRecord> getEarthquakeByTimeRange(String range) {
        LocalDateTime startTime;
        switch (range) {
            case "24h":
                startTime = LocalDateTime.now().minusHours(24);
                break;
            case "30d":
                startTime = LocalDateTime.now().minusDays(30);
                break;
            case "7d":
            default:
                startTime = LocalDateTime.now().minusDays(7);
                break;
        }
        return earthquakeRecordMapper.selectByTimeRange(startTime);
    }

    @Override
    public List<EarthquakeRecord> getEarthquakeByFilters(LocalDateTime startTime, LocalDateTime endTime, BigDecimal minMagnitude, BigDecimal maxMagnitude, BigDecimal minDepth, BigDecimal maxDepth) {
        return earthquakeRecordMapper.selectByFilters(startTime, endTime, minMagnitude, maxMagnitude, minDepth, maxDepth);
    }
}
