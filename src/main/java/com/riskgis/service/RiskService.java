package com.riskgis.service;

import com.riskgis.dto.request.UnderwritingRequest;
import com.riskgis.model.*;

import java.util.List;
import java.util.Map;

public interface RiskService {
    List<RiskAssessment> getRiskData(String type, String level, String bounds, Integer limit, Integer offset);
    Map<String, Object> getRiskStats(String type, String timeRange);
    List<DisasterRecord> getDisasterRecords(String type, String startTime, String endTime, String bounds, Integer limit, Integer offset);
    List<Warning> getWarnings(String type, String level, String timeRange);
    InsuranceTarget submitUnderwriting(UnderwritingRequest request);
    InsuranceTarget getUnderwritingResult(Long id);
    List<Map<String, Object>> getClaims(String status, String disasterType, Integer limit, Integer offset);
    Map<String, Object> handleClaim(Long id, String action, String comment, Double amount);
}
