package com.riskgis.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpatialQueryRequest {
    private String geometry;
    private String layerId;
    private Double buffer;
}
