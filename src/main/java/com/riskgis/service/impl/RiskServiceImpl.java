package com.riskgis.service.impl;

import com.riskgis.dto.request.UnderwritingRequest;
import com.riskgis.mapper.*;
import com.riskgis.model.*;
import com.riskgis.service.RiskService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class RiskServiceImpl implements RiskService {

    private final RiskAssessmentMapper riskAssessmentMapper;
    private final DisasterRecordMapper disasterRecordMapper;
    private final WarningMapper warningMapper;
    private final InsuranceTargetMapper insuranceTargetMapper;

    public RiskServiceImpl(RiskAssessmentMapper riskAssessmentMapper,
                           DisasterRecordMapper disasterRecordMapper,
                           WarningMapper warningMapper,
                           InsuranceTargetMapper insuranceTargetMapper) {
        this.riskAssessmentMapper = riskAssessmentMapper;
        this.disasterRecordMapper = disasterRecordMapper;
        this.warningMapper = warningMapper;
        this.insuranceTargetMapper = insuranceTargetMapper;
    }

    @Override
    public List<RiskAssessment> getRiskData(String type, String level, String bounds, Integer limit, Integer offset) {
        if (bounds != null && !bounds.isEmpty()) {
            String[] parts = bounds.split(",");
            if (parts.length == 4) {
                double minLng = Double.parseDouble(parts[0]);
                double minLat = Double.parseDouble(parts[1]);
                double maxLng = Double.parseDouble(parts[2]);
                double maxLat = Double.parseDouble(parts[3]);
                return riskAssessmentMapper.selectByBounds(minLng, minLat, maxLng, maxLat);
            }
        }

        if (type != null && !type.isEmpty()) {
            return riskAssessmentMapper.selectByRiskType(type);
        }

        if (level != null && !level.isEmpty()) {
            return riskAssessmentMapper.selectByRiskLevel(level);
        }

        return riskAssessmentMapper.selectList(null);
    }

    @Override
    public Map<String, Object> getRiskStats(String type, String timeRange) {
        Map<String, Object> stats = new HashMap<>();

        List<Map<String, Object>> levelCounts = riskAssessmentMapper.countByRiskLevel();
        Map<String, Long> byLevel = new HashMap<>();
        for (Map<String, Object> row : levelCounts) {
            byLevel.put((String) row.get("risk_level"), (Long) row.get("count"));
        }
        stats.put("byLevel", byLevel);

        List<Map<String, Object>> typeCounts = disasterRecordMapper.countByDisasterType();
        Map<String, Long> byType = new HashMap<>();
        for (Map<String, Object> row : typeCounts) {
            byType.put((String) row.get("disaster_type"), (Long) row.get("count"));
        }
        stats.put("byDisasterType", byType);

        stats.put("totalRiskAssessments", riskAssessmentMapper.selectCount(null));
        stats.put("totalDisasters", disasterRecordMapper.selectCount(null));
        stats.put("activeWarnings", warningMapper.selectByStatus("active").size());

        return stats;
    }

    @Override
    public List<DisasterRecord> getDisasterRecords(String type, String startTime, String endTime,
                                                    String bounds, Integer limit, Integer offset) {
        if (bounds != null && !bounds.isEmpty()) {
            String[] parts = bounds.split(",");
            if (parts.length == 4) {
                double minLng = Double.parseDouble(parts[0]);
                double minLat = Double.parseDouble(parts[1]);
                double maxLng = Double.parseDouble(parts[2]);
                double maxLat = Double.parseDouble(parts[3]);
                return disasterRecordMapper.selectByBounds(minLng, minLat, maxLng, maxLat);
            }
        }

        if (type != null && !type.isEmpty()) {
            return disasterRecordMapper.selectByDisasterType(type);
        }

        return disasterRecordMapper.selectList(null);
    }

    @Override
    public List<Warning> getWarnings(String type, String level, String timeRange) {
        if (type != null && !type.isEmpty()) {
            return warningMapper.selectByWarningType(type);
        }

        if (level != null && !level.isEmpty()) {
            return warningMapper.selectByLevel(level);
        }

        return warningMapper.selectByStatus("active");
    }

    @Override
    @Transactional
    public InsuranceTarget submitUnderwriting(UnderwritingRequest request) {
        InsuranceTarget target = new InsuranceTarget();
        target.setTargetName(request.getName());
        target.setTargetType(request.getType());
        target.setCoverageAmount(request.getAmount());

        BigDecimal riskScore = new BigDecimal(50 + Math.random() * 30);
        target.setRiskScore(riskScore);
        target.setPremiumRate(riskScore.divide(new BigDecimal(1000), 4, BigDecimal.ROUND_HALF_UP));

        insuranceTargetMapper.insert(target);

        return target;
    }

    @Override
    public InsuranceTarget getUnderwritingResult(Long id) {
        return insuranceTargetMapper.selectById(id);
    }

    @Override
    public List<Map<String, Object>> getClaims(String status, String disasterType, Integer limit, Integer offset) {
        return new ArrayList<>();
    }

    @Override
    @Transactional
    public Map<String, Object> handleClaim(Long id, String action, String comment, Double amount) {
        Map<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("action", action);
        result.put("status", "processed");
        return result;
    }
}
