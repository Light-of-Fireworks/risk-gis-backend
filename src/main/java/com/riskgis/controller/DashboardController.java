package com.riskgis.controller;

import com.riskgis.dto.response.ApiResponse;
import com.riskgis.mapper.InsurancePolicyMapper;
import com.riskgis.mapper.WarningMapper;
import com.riskgis.mapper.FloodWarningMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final InsurancePolicyMapper insurancePolicyMapper;
    private final WarningMapper warningMapper;
    private final FloodWarningMapper floodWarningMapper;

    public DashboardController(InsurancePolicyMapper insurancePolicyMapper,
                               WarningMapper warningMapper,
                               FloodWarningMapper floodWarningMapper) {
        this.insurancePolicyMapper = insurancePolicyMapper;
        this.warningMapper = warningMapper;
        this.floodWarningMapper = floodWarningMapper;
    }

    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();

        // 承保标的数量
        long policyCount = insurancePolicyMapper.selectCount(null);
        stats.put("insuranceTargets", policyCount);

        // 本月灾害预警（warning 表 + flood_warning 表）
        LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        long warningCount = warningMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.riskgis.model.Warning>()
                        .ge(com.riskgis.model.Warning::getCreatedAt, monthStart)
        );
        long floodWarningCount = floodWarningMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.riskgis.model.FloodWarning>()
                        .ge(com.riskgis.model.FloodWarning::getCreatedAt, monthStart)
        );
        stats.put("monthlyWarnings", warningCount + floodWarningCount);

        // 高风险区域（暂时使用固定值，后续可从风险评估表查询）
        stats.put("highRiskAreas", 45);

        return ApiResponse.success(stats);
    }
}
