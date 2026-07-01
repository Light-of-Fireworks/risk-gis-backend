package com.riskgis.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.riskgis.client.FloodWarningClient;
import com.riskgis.dto.flood.FloodWarningResponse;
import com.riskgis.mapper.FloodWarningMapper;
import com.riskgis.model.FloodWarning;
import com.riskgis.service.FloodWarningService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class FloodWarningServiceImpl implements FloodWarningService {

    private static final Logger log = LoggerFactory.getLogger(FloodWarningServiceImpl.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final FloodWarningMapper floodWarningMapper;
    private final FloodWarningClient floodWarningClient;

    public FloodWarningServiceImpl(FloodWarningMapper floodWarningMapper, FloodWarningClient floodWarningClient) {
        this.floodWarningMapper = floodWarningMapper;
        this.floodWarningClient = floodWarningClient;
    }

    @Override
    public void syncFloodWarningData() {
        try {
            FloodWarningResponse response = floodWarningClient.getWarnings();

            if (response == null || response.getData() == null) {
                return;
            }

            List<FloodWarningResponse.FloodWarningItem> dataList = response.getData();
            if (dataList.isEmpty()) {
                return;
            }

            int inserted = 0;
            for (FloodWarningResponse.FloodWarningItem item : dataList) {
                try {
                    Long wrInfoId = Long.valueOf(item.getWrInfoId());

                    LambdaQueryWrapper<FloodWarning> wrapper = new LambdaQueryWrapper<>();
                    wrapper.eq(FloodWarning::getWrInfoId, wrInfoId);
                    Long count = floodWarningMapper.selectCount(wrapper);
                    if (count > 0) {
                        continue;
                    }

                    FloodWarning warning = new FloodWarning();
                    warning.setWrInfoId(wrInfoId);
                    warning.setWrIcon(item.getWrIcon());
                    warning.setWrTitle(item.getWrTitle());
                    warning.setWrDetail(item.getWrDetail());
                    warning.setPublishTime(LocalDateTime.parse(item.getPublishTime(), FORMATTER));
                    warning.setExpireTime(LocalDateTime.parse(item.getExpireTime(), FORMATTER));
                    warning.setLongitude(new BigDecimal(item.getLongitude()));
                    warning.setLatitude(new BigDecimal(item.getLatitude()));
                    warning.setWrType(item.getWrType());
                    warning.setWrLevel(item.getWrLevel());
                    warning.setInfluenceArea(item.getInfluenceArea());
                    warning.setInfluenceAreaCd(item.getInfluenceAreaCd());
                    warning.setUnitName(item.getUnitName());
                    warning.setDetailUrl(item.getUrl());

                    floodWarningMapper.insert(warning);
                    inserted++;
                } catch (Exception e) {
                    log.warn("解析洪水预警数据失败: {}", e.getMessage());
                }
            }

            if (inserted > 0) {
                log.info("洪水预警数据同步完成，新增 {} 条记录", inserted);
            }
        } catch (Exception e) {
            log.error("洪水预警数据同步失败: {}", e.getMessage());
        }
    }

    @Override
    public List<FloodWarning> getFloodWarningsByTimeRange(String range) {
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
        return floodWarningMapper.selectByTimeRange(startTime);
    }

    @Override
    public List<FloodWarning> getFloodWarningsByDateRange(LocalDateTime startTime, LocalDateTime endTime) {
        return floodWarningMapper.selectByDateRange(startTime, endTime);
    }
}
