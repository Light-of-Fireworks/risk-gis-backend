package com.riskgis.dto.opentopodata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenTopoDataResponse {

    private List<ResultItem> results;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ResultItem {
        private Double elevation;
    }
}
