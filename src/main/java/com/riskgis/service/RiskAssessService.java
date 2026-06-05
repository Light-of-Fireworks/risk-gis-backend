package com.riskgis.service;

import com.riskgis.dto.request.AreaAssessRequest;
import com.riskgis.dto.request.PointAssessRequest;
import com.riskgis.dto.response.RiskAssessResponse;
import java.util.List;

public interface RiskAssessService {
    RiskAssessResponse assessPoint(PointAssessRequest request);
    RiskAssessResponse assessArea(AreaAssessRequest request);
    RiskAssessResponse assessRegion(String regionCode, List<String> disasterTypes);
}
