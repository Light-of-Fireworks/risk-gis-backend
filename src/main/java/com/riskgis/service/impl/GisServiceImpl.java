package com.riskgis.service.impl;

import com.riskgis.dto.request.SpatialQueryRequest;
import com.riskgis.mapper.GeoDataMapper;
import com.riskgis.model.GeoData;
import com.riskgis.service.GisService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GisServiceImpl implements GisService {

    private final GeoDataMapper geoDataMapper;

    public GisServiceImpl(GeoDataMapper geoDataMapper) {
        this.geoDataMapper = geoDataMapper;
    }

    @Override
    public List<GeoData> getGeoData(String type, String bounds, Integer limit, Integer offset) {
        if (bounds != null && !bounds.isEmpty()) {
            String[] parts = bounds.split(",");
            if (parts.length == 4) {
                double minLng = Double.parseDouble(parts[0]);
                double minLat = Double.parseDouble(parts[1]);
                double maxLng = Double.parseDouble(parts[2]);
                double maxLat = Double.parseDouble(parts[3]);
                return geoDataMapper.selectByBounds(minLng, minLat, maxLng, maxLat);
            }
        }

        if (type != null && !type.isEmpty()) {
            return geoDataMapper.selectByType(type);
        }

        return geoDataMapper.selectList(null);
    }

    @Override
    public GeoData getLayerData(String layerId) {
        return geoDataMapper.selectById(Long.parseLong(layerId));
    }

    @Override
    public List<GeoData> spatialQuery(SpatialQueryRequest request) {
        if (request.getGeometry() != null && !request.getGeometry().isEmpty()) {
            return geoDataMapper.selectByGeometry(request.getGeometry());
        }
        return geoDataMapper.selectList(null);
    }

    @Override
    public Map<String, Object> spatialAnalysis(String type, String geometry, Map<String, Object> parameters) {
        Map<String, Object> result = new HashMap<>();
        result.put("type", type);
        result.put("status", "completed");
        return result;
    }

    @Override
    public Map<String, Object> geocode(String address) {
        Map<String, Object> result = new HashMap<>();
        result.put("address", address);
        result.put("lng", 116.397428);
        result.put("lat", 39.90923);
        return result;
    }

    @Override
    public Map<String, Object> reverseGeocode(String lnglat) {
        Map<String, Object> result = new HashMap<>();
        result.put("lnglat", lnglat);
        result.put("address", "北京市东城区");
        return result;
    }
}
