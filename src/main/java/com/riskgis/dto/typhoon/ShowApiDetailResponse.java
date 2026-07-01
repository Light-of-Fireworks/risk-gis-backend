package com.riskgis.dto.typhoon;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ShowApiDetailResponse {

    @JsonProperty("showapi_res_body")
    private ShowApiResBody showapiResBody;

    @Data
    public static class ShowApiResBody {
        @JsonProperty("obj")
        private TyphoonDetail detail;
    }

    @Data
    public static class TyphoonDetail {
        private String tfid;
        private String name;
        private String enname;
        private String isactive;
        private List<TyphoonPointDTO> points;
    }
}