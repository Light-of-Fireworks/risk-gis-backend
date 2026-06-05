package com.riskgis.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RiskAssessResponse {
    private List<DisasterAssessment> assessments;
    private String overallRiskLevel;
    private BigDecimal overallRiskScore;
    private Map<String, Double> areaDistribution;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DisasterAssessment {
        private String disasterType;
        private String disasterTypeName;
        private BigDecimal riskScore;
        private String riskLevel;
        private String riskLevelName;
        private Map<String, FactorDetail> factors;
        private Integer gridCount;
        private Map<String, Double> areaDistribution;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FactorDetail {
        private Object value;
        private BigDecimal score;
        private BigDecimal weight;
    }
}
