package com.riskgis.dto.earthquake;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class EarthquakeResponse {

    @JsonProperty("data")
    private List<EarthquakeItem> data;

    @Data
    public static class EarthquakeItem {
        private String leve;
        private String weidu;
        private String jingdu;
        private String shendu;
        private String weizhi;
        private String addtime;
        private String hctime;
    }
}