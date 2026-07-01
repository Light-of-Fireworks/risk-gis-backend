package com.riskgis.dto.amap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AmapInputTipsResponse {
    private List<Object> tips;
}
