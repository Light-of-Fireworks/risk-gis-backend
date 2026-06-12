package com.riskgis.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TyphoonPolicyQueryRequest {
    private List<TyphoonData> typhoons;
    private Integer bufferRadius;  // km, 10-100
    private String endDate;        // optional, yyyy-MM-dd
    private List<String> orgCodes;       // optional
    private List<String> categoryCodes;  // optional
    private List<String> typeCodes;      // optional

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TyphoonData {
        private String tfid;
        private String typhoonName;
        private List<PointData> points;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PointData {
        private Double lng;
        private Double lat;
    }
}
