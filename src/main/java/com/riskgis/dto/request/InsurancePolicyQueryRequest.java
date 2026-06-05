package com.riskgis.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InsurancePolicyQueryRequest {
    private Double lng;
    private Double lat;
    private Integer radius;
    private List<String> categoryCodes;
    private List<String> typeCodes;
}
