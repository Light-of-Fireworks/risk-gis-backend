package com.riskgis.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TyphoonPolicyStatsResponse {
    private List<TyphoonStats> typhoons;
    private TotalStats total;
    private List<InsurancePolicyGroupVO> policyGroups;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TyphoonStats {
        private String tfid;
        private String typhoonName;
        private Integer targetCount;
        private BigDecimal coverageAmount;
        private BigDecimal premium;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TotalStats {
        private Integer targetCount;
        private BigDecimal coverageAmount;
        private BigDecimal premium;
    }
}
