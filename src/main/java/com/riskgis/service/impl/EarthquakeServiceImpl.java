package com.riskgis.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.riskgis.mapper.EarthquakeRecordMapper;
import com.riskgis.model.EarthquakeRecord;
import com.riskgis.service.EarthquakeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class EarthquakeServiceImpl implements EarthquakeService {

    private static final Logger log = LoggerFactory.getLogger(EarthquakeServiceImpl.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final EarthquakeRecordMapper earthquakeRecordMapper;

    @Value("${apihz.id}")
    private String apihzId;

    @Value("${apihz.key}")
    private String apihzKey;

    public EarthquakeServiceImpl(EarthquakeRecordMapper earthquakeRecordMapper) {
        this.earthquakeRecordMapper = earthquakeRecordMapper;
    }

    @Override
    public void syncEarthquakeData() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://cn.apihz.cn/api/tianqi/dizhen.php?id=" + apihzId + "&key=" + apihzKey;
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response == null || !response.containsKey("data")) {
                return;
            }

            List<?> dataList = (List<?>) response.get("data");
            if (dataList == null || dataList.isEmpty()) {
                return;
            }

            int inserted = 0;
            for (Object item : dataList) {
                Map<String, Object> data = (Map<String, Object>) item;
                try {
                    EarthquakeRecord record = new EarthquakeRecord();
                    record.setOccurTime(LocalDateTime.parse((String) data.get("addtime"), FORMATTER));
                    record.setMagnitude(new BigDecimal((String) data.get("leve")));
                    record.setLatitude(new BigDecimal((String) data.get("weidu")));
                    record.setLongitude(new BigDecimal((String) data.get("jingdu")));
                    record.setDepth(new BigDecimal((String) data.get("shendu")));
                    record.setLocation((String) data.get("weizhi"));
                    record.setReportTime(LocalDateTime.parse((String) data.get("hctime"), FORMATTER));

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
