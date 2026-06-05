package com.riskgis.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InsurancePolicyGroupVO {
    private Double lng;
    private Double lat;
    private Integer count;
    private List<InsurancePolicyVO> policies;
}
