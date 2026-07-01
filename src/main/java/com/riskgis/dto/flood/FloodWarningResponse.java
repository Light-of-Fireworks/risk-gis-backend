package com.riskgis.dto.flood;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class FloodWarningResponse {

    @JsonProperty("Data")
    private List<FloodWarningItem> data;

    @Data
    public static class FloodWarningItem {
        @JsonProperty("WRInfoID")
        private String wrInfoId;

        @JsonProperty("WRIcon")
        private String wrIcon;

        @JsonProperty("WRTitle")
        private String wrTitle;

        @JsonProperty("WRDetail")
        private String wrDetail;

        @JsonProperty("IYMDH")
        private String publishTime;

        @JsonProperty("EYMDH")
        private String expireTime;

        @JsonProperty("LGTD")
        private String longitude;

        @JsonProperty("LTTD")
        private String latitude;

        @JsonProperty("WRType")
        private String wrType;

        @JsonProperty("WRLevel")
        private String wrLevel;

        @JsonProperty("InfluadArea")
        private String influenceArea;

        @JsonProperty("InfluadAreaCd")
        private String influenceAreaCd;

        @JsonProperty("UnitName")
        private String unitName;

        @JsonProperty("Url")
        private String url;
    }
}