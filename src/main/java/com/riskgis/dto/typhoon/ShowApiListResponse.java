package com.riskgis.dto.typhoon;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ShowApiListResponse {

    @JsonProperty("showapi_res_body")
    private ShowApiResBody showapiResBody;

    @Data
    public static class ShowApiResBody {
        @JsonProperty("typhoon_list")
        private List<TyphoonItem> typhoonList;
    }

    @Data
    public static class TyphoonItem {
        private String tfid;
        private String name;
        private String en;
    }
}