package com.riskgis.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.riskgis.mapper.FloodWarningMapper;
import com.riskgis.model.FloodWarning;
import com.riskgis.service.FloodWarningService;
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
public class FloodWarningServiceImpl implements FloodWarningService {

    private static final Logger log = LoggerFactory.getLogger(FloodWarningServiceImpl.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final FloodWarningMapper floodWarningMapper;

    @Value("${flood-warning.api-url}")
    private String apiUrl;

    public FloodWarningServiceImpl(FloodWarningMapper floodWarningMapper) {
        this.floodWarningMapper = floodWarningMapper;
    }

    @Override
    public void syncFloodWarningData() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            Map<String, Object> response = restTemplate.getForObject(apiUrl, Map.class);

            if (response == null || !response.containsKey("Data")) {
                return;
            }

            List<?> dataList = (List<?>) response.get("Data");
            if (dataList == null || dataList.isEmpty()) {
                return;
            }

            int inserted = 0;
            for (Object item : dataList) {
                Map<String, Object> data = (Map<String, Object>) item;
                try {
                    Long wrInfoId = Long.valueOf(data.get("WRInfoID").toString());

                    LambdaQueryWrapper<FloodWarning> wrapper = new LambdaQueryWrapper<>();
                    wrapper.eq(FloodWarning::getWrInfoId, wrInfoId);
                    Long count = floodWarningMapper.selectCount(wrapper);
                    if (count > 0) {
                        continue;
                    }

                    FloodWarning warning = new FloodWarning();
                    warning.setWrInfoId(wrInfoId);
                    warning.setWrIcon((String) data.get("WRIcon"));
                    warning.setWrTitle((String) data.get("WRTitle"));
                    warning.setWrDetail((String) data.get("WRDetail"));
                    warning.setPublishTime(LocalDateTime.parse((String) data.get("IYMDH"), FORMATTER));
                    warning.setExpireTime(LocalDateTime.parse((String) data.get("EYMDH"), FORMATTER));
                    warning.setLongitude(new BigDecimal((String) data.get("LGTD")));
                    warning.setLatitude(new BigDecimal((String) data.get("LTTD")));
                    warning.setWrType((String) data.get("WRType"));
                    warning.setWrLevel((String) data.get("WRLevel"));
                    warning.setInfluenceArea((String) data.get("InfluadArea"));
                    warning.setInfluenceAreaCd((String) data.get("InfluadAreaCd"));
                    warning.setUnitName((String) data.get("UnitName"));
                    warning.setDetailUrl((String) data.get("Url"));

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
