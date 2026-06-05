package com.riskgis.service.impl;

import com.riskgis.dto.request.AreaAssessRequest;
import com.riskgis.dto.request.PointAssessRequest;
import com.riskgis.dto.response.RiskAssessResponse;
import com.riskgis.dto.response.RiskAssessResponse.DisasterAssessment;
import com.riskgis.dto.response.RiskAssessResponse.FactorDetail;
import com.riskgis.mapper.GeoDataMapper;
import com.riskgis.mapper.RiskFactorConfigMapper;
import com.riskgis.mapper.RiskGridMapper;
import com.riskgis.model.GeoData;
import com.riskgis.model.RiskGrid;
import com.riskgis.model.enums.DisasterType;
import com.riskgis.model.enums.RiskLevel;
import com.riskgis.service.RiskAssessService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RiskAssessServiceImpl implements RiskAssessService {

    private final RiskGridMapper riskGridMapper;
    private final RiskFactorConfigMapper riskFactorConfigMapper;
    private final GeoDataMapper geoDataMapper;
    private final ObjectMapper objectMapper;

    public RiskAssessServiceImpl(RiskGridMapper riskGridMapper,
                                  RiskFactorConfigMapper riskFactorConfigMapper,
                                  GeoDataMapper geoDataMapper,
                                  ObjectMapper objectMapper) {
        this.riskGridMapper = riskGridMapper;
        this.riskFactorConfigMapper = riskFactorConfigMapper;
        this.geoDataMapper = geoDataMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public RiskAssessResponse assessPoint(PointAssessRequest request) {
        List<String> types = request.getDisasterTypes();
        if (types == null || types.isEmpty()) {
            types = Arrays.stream(DisasterType.values())
                    .map(Enum::name)
                    .collect(Collectors.toList());
        }

        List<RiskGrid> grids = riskGridMapper.selectByPoint(
                request.getLongitude(), request.getLatitude(), types);

        List<DisasterAssessment> assessments = new ArrayList<>();
        for (RiskGrid grid : grids) {
            DisasterAssessment da = new DisasterAssessment();
            da.setDisasterType(grid.getDisasterType());
            da.setDisasterTypeName(getDisasterTypeName(grid.getDisasterType()));
            da.setRiskScore(grid.getRiskScore());
            da.setRiskLevel(grid.getRiskLevel());
            da.setRiskLevelName(RiskLevel.valueOf(grid.getRiskLevel()).getLabel());
            da.setFactors(parseFactors(grid.getFactors()));
            assessments.add(da);
        }

        RiskAssessResponse response = new RiskAssessResponse();
        response.setAssessments(assessments);
        response.setOverallRiskLevel(calculateOverallLevel(assessments));
        response.setOverallRiskScore(calculateOverallScore(assessments));
        return response;
    }

    @Override
    public RiskAssessResponse assessArea(AreaAssessRequest request) {
        List<String> types = request.getDisasterTypes();
        if (types == null || types.isEmpty()) {
            types = Arrays.stream(DisasterType.values())
                    .map(Enum::name)
                    .collect(Collectors.toList());
        }

        List<DisasterAssessment> assessments = new ArrayList<>();
        for (String type : types) {
            List<Map<String, Object>> rows = riskGridMapper.selectByArea(request.getGeometry(), type);
            if (rows.isEmpty()) continue;

            BigDecimal totalArea = BigDecimal.ZERO;
            BigDecimal weightedScore = BigDecimal.ZERO;
            Map<String, Double> levelArea = new LinkedHashMap<>();

            for (Map<String, Object> row : rows) {
                BigDecimal area = new BigDecimal(row.get("intersect_area").toString());
                BigDecimal score = new BigDecimal(row.get("risk_score").toString());
                String level = (String) row.get("risk_level");

                totalArea = totalArea.add(area);
                weightedScore = weightedScore.add(score.multiply(area));
                levelArea.merge(level, area.doubleValue(), Double::sum);
            }

            if (totalArea.compareTo(BigDecimal.ZERO) == 0) continue;

            BigDecimal avgScore = weightedScore.divide(totalArea, 2, RoundingMode.HALF_UP);

            Map<String, Double> distribution = new LinkedHashMap<>();
            for (Map.Entry<String, Double> entry : levelArea.entrySet()) {
                distribution.put(entry.getKey(), entry.getValue() / totalArea.doubleValue());
            }

            DisasterAssessment da = new DisasterAssessment();
            da.setDisasterType(type);
            da.setDisasterTypeName(getDisasterTypeName(type));
            da.setRiskScore(avgScore);
            da.setRiskLevel(RiskLevel.fromScore(avgScore.doubleValue()).name());
            da.setRiskLevelName(RiskLevel.fromScore(avgScore.doubleValue()).getLabel());
            da.setGridCount(rows.size());
            da.setAreaDistribution(distribution);
            assessments.add(da);
        }

        RiskAssessResponse response = new RiskAssessResponse();
        response.setAssessments(assessments);
        response.setOverallRiskLevel(calculateOverallLevel(assessments));
        response.setOverallRiskScore(calculateOverallScore(assessments));
        return response;
    }

    @Override
    public RiskAssessResponse assessRegion(String regionCode, List<String> disasterTypes) {
        List<GeoData> regions = geoDataMapper.selectByRegionCode(regionCode);
        if (regions.isEmpty()) {
            RiskAssessResponse response = new RiskAssessResponse();
            response.setAssessments(Collections.emptyList());
            response.setOverallRiskLevel("LOW");
            response.setOverallRiskScore(BigDecimal.ZERO);
            return response;
        }

        String wkt = regions.get(0).getGeometry();
        AreaAssessRequest areaRequest = new AreaAssessRequest();
        areaRequest.setGeometry(wkt);
        areaRequest.setDisasterTypes(disasterTypes);
        return assessArea(areaRequest);
    }

    private String getDisasterTypeName(String type) {
        try {
            return DisasterType.valueOf(type).getLabel();
        } catch (IllegalArgumentException e) {
            return type;
        }
    }

    private Map<String, FactorDetail> parseFactors(String factorsJson) {
        if (factorsJson == null || factorsJson.isEmpty()) return Collections.emptyMap();
        try {
            Map<String, Object> raw = objectMapper.readValue(factorsJson,
                    new TypeReference<Map<String, Object>>() {});
            Map<String, FactorDetail> result = new LinkedHashMap<>();
            for (Map.Entry<String, Object> entry : raw.entrySet()) {
                if (entry.getValue() instanceof Map) {
                    Map<String, Object> m = (Map<String, Object>) entry.getValue();
                    FactorDetail fd = new FactorDetail();
                    fd.setValue(m.get("value"));
                    fd.setScore(new BigDecimal(m.get("score").toString()));
                    fd.setWeight(new BigDecimal(m.get("weight").toString()));
                    result.put(entry.getKey(), fd);
                }
            }
            return result;
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    private String calculateOverallLevel(List<DisasterAssessment> assessments) {
        if (assessments.isEmpty()) return "LOW";
        double maxScore = assessments.stream()
                .mapToDouble(a -> a.getRiskScore().doubleValue())
                .max().orElse(0);
        return RiskLevel.fromScore(maxScore).name();
    }

    private BigDecimal calculateOverallScore(List<DisasterAssessment> assessments) {
        if (assessments.isEmpty()) return BigDecimal.ZERO;
        double avg = assessments.stream()
                .mapToDouble(a -> a.getRiskScore().doubleValue())
                .average().orElse(0);
        return BigDecimal.valueOf(avg).setScale(2, RoundingMode.HALF_UP);
    }
}
