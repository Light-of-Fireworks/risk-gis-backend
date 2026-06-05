package com.riskgis.service;

import com.riskgis.dto.request.SpatialQueryRequest;
import com.riskgis.model.GeoData;

import java.util.List;
import java.util.Map;

public interface GisService {
    List<GeoData> getGeoData(String type, String bounds, Integer limit, Integer offset);
    GeoData getLayerData(String layerId);
    List<GeoData> spatialQuery(SpatialQueryRequest request);
    Map<String, Object> spatialAnalysis(String type, String geometry, Map<String, Object> parameters);
    Map<String, Object> geocode(String address);
    Map<String, Object> reverseGeocode(String lnglat);
}
