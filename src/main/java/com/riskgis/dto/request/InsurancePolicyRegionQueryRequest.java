package com.riskgis.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InsurancePolicyRegionQueryRequest {
    private String geometry;  // WKT 格式的几何对象（Polygon）
    private List<String> categoryCodes;
    private List<String> typeCodes;
}
