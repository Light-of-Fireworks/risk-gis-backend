package com.riskgis.dto.amap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AmapRegeoResponse {
    private Object regeocode;
}
