package com.riskgis.config;

import com.riskgis.mapper.RiskFactorConfigMapper;
import com.riskgis.mapper.RiskGridMapper;
import com.riskgis.model.RiskFactorConfig;
import com.riskgis.model.RiskGrid;
import com.riskgis.model.enums.DisasterType;
import com.riskgis.model.enums.RiskLevel;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class RiskDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(RiskDataInitializer.class);

    private final RiskGridMapper riskGridMapper;
    private final RiskFactorConfigMapper riskFactorConfigMapper;
    private final ObjectMapper objectMapper;

    // 中国大陆范围
    private static final double MIN_LNG = 73.0;
    private static final double MAX_LNG = 135.0;
    private static final double MIN_LAT = 3.0;
    private static final double MAX_LAT = 53.0;
    private static final double GRID_STEP = 1.0; // 约 100km

    // 从数据库加载的因子配置：disaster_type -> List<RiskFactorConfig>
    private Map<String, List<RiskFactorConfig>> factorConfigMap;

    public RiskDataInitializer(RiskGridMapper riskGridMapper,
                                RiskFactorConfigMapper riskFactorConfigMapper,
                                ObjectMapper objectMapper) {
        this.riskGridMapper = riskGridMapper;
        this.riskFactorConfigMapper = riskFactorConfigMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) throws Exception {
        if (riskGridMapper.selectCount(null) > 0) {
            log.info("风险网格数据已存在，跳过初始化");
            return;
        }

        // 从数据库加载因子配置
        List<RiskFactorConfig> allConfigs = riskFactorConfigMapper.selectList(null);
        if (allConfigs.isEmpty()) {
            log.warn("risk_factor_config 表为空，请先执行 init_schema.sql 中的种子数据插入");
            return;
        }
        factorConfigMap = allConfigs.stream()
                .collect(Collectors.groupingBy(RiskFactorConfig::getDisasterType));
        log.info("已加载 {} 种灾害类型的因子配置", factorConfigMap.size());

        log.info("开始初始化风险网格数据...");
        List<RiskGrid> batch = new ArrayList<>();
        for (double lng = MIN_LNG; lng < MAX_LNG; lng += GRID_STEP) {
            for (double lat = MIN_LAT; lat < MAX_LAT; lat += GRID_STEP) {
                String wkt = String.format("POLYGON((%f %f,%f %f,%f %f,%f %f,%f %f))",
                        lng, lat,
                        lng + GRID_STEP, lat,
                        lng + GRID_STEP, lat + GRID_STEP,
                        lng, lat + GRID_STEP,
                        lng, lat);

                for (DisasterType dt : DisasterType.values()) {
                    double score = calculateScore(dt, lng, lat);
                    RiskLevel level = RiskLevel.fromScore(score);

                    RiskGrid grid = new RiskGrid();
                    grid.setDisasterType(dt.name());
                    grid.setRiskScore(BigDecimal.valueOf(score));
                    grid.setRiskLevel(level.name());
                    grid.setFactors(buildFactorsJson(dt, lng, lat, score));
                    grid.setGeometry(wkt);
                    grid.setGridSize(100000);
                    batch.add(grid);
                }
            }
        }

        // 批量插入
        for (RiskGrid grid : batch) {
            riskGridMapper.insert(grid);
        }
        log.info("风险网格数据初始化完成，共 {} 条记录", batch.size());
    }

    private double calculateScore(DisasterType type, double lng, double lat) {
        Random rand = new Random(hash(type.name(), lng, lat));
        switch (type) {
            case EARTHQUAKE:
                if (lng > 97 && lng < 105 && lat > 25 && lat < 35) return 60 + rand.nextDouble() * 30;
                if (lng > 110 && lng < 120 && lat > 35 && lat < 42) return 50 + rand.nextDouble() * 25;
                if (lng > 120 && lat > 22 && lat < 26) return 55 + rand.nextDouble() * 30;
                return 10 + rand.nextDouble() * 25;
            case TYPHOON:
                if (lng > 115 && lat > 18 && lat < 30) return 55 + rand.nextDouble() * 35;
                if (lng > 110 && lat > 20 && lat < 25) return 50 + rand.nextDouble() * 30;
                return 5 + rand.nextDouble() * 20;
            case FLOOD:
            case FLOOD_RIVER:
                if (lng > 105 && lng < 122 && lat > 25 && lat < 33) return 50 + rand.nextDouble() * 40;
                if (lng > 110 && lng < 117 && lat > 22 && lat < 26) return 45 + rand.nextDouble() * 35;
                return 10 + rand.nextDouble() * 25;
            case HEAVY_RAIN:
                if (lng > 105 && lat > 20 && lat < 28) return 45 + rand.nextDouble() * 40;
                return 10 + rand.nextDouble() * 30;
            case SNOWSTORM:
                if (lat > 40) return 40 + rand.nextDouble() * 40;
                if (lng > 80 && lng < 100 && lat > 28) return 35 + rand.nextDouble() * 35;
                return 5 + rand.nextDouble() * 15;
            case HAIL:
                if (lat > 35 && lat < 50 && lng > 100) return 30 + rand.nextDouble() * 40;
                return 5 + rand.nextDouble() * 20;
            case LIGHTNING:
                if (lat < 28 && lng > 105) return 35 + rand.nextDouble() * 40;
                return 10 + rand.nextDouble() * 25;
            case STORM_SURGE:
                if (lng > 115 && lat > 20 && lat < 40) return 40 + rand.nextDouble() * 40;
                return 2 + rand.nextDouble() * 10;
            case LANDSLIDE:
                if (lng > 98 && lng < 110 && lat > 25 && lat < 35) return 50 + rand.nextDouble() * 40;
                if (lng > 103 && lng < 112 && lat > 33 && lat < 40) return 35 + rand.nextDouble() * 35;
                return 5 + rand.nextDouble() * 20;
            default:
                return 10 + rand.nextDouble() * 30;
        }
    }

    private String buildFactorsJson(DisasterType type, double lng, double lat, double score) {
        try {
            List<RiskFactorConfig> configs = factorConfigMap.get(type.name());
            if (configs == null || configs.isEmpty()) return "{}";

            Map<String, Object> factors = new LinkedHashMap<>();
            Random rand = new Random(hash(type.name(), lng, lat) + 1);

            double remaining = score;
            for (int i = 0; i < configs.size(); i++) {
                RiskFactorConfig cfg = configs.get(i);
                double weight = cfg.getFactorWeight().doubleValue();
                Map<String, Object> f = new LinkedHashMap<>();
                double factorScore = (i == configs.size() - 1) ? remaining :
                        remaining * weight + (rand.nextDouble() - 0.5) * 10;
                factorScore = Math.max(0, Math.min(100, factorScore));
                remaining -= factorScore * weight;
                f.put("value", Math.round(factorScore * 10) / 10.0);
                f.put("score", Math.round(factorScore * 100) / 100.0);
                f.put("weight", weight);
                factors.put(cfg.getFactorName(), f);
            }
            return objectMapper.writeValueAsString(factors);
        } catch (Exception e) {
            return "{}";
        }
    }

    private int hash(String type, double lng, double lat) {
        return Objects.hash(type, (int)(lng * 10), (int)(lat * 10));
    }
}
