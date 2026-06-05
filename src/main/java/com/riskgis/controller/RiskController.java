package com.riskgis.controller;

import com.riskgis.dto.request.UnderwritingRequest;
import com.riskgis.dto.response.ApiResponse;
import com.riskgis.model.*;
import com.riskgis.service.RiskService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 风险管理控制器
 * <p>
 * 提供风险评估、灾害预警、承保理赔相关的 API 接口。
 * </p>
 */
@RestController
@RequestMapping("/risk")
public class RiskController {

    private final RiskService riskService;

    public RiskController(RiskService riskService) {
        this.riskService = riskService;
    }

    /**
     * 获取风险数据
     * <p>
     * 获取风险数据列表，支持按类型、等级和边界框进行过滤。
     * </p>
     *
     * @param type   风险类型（可选）
     * @param level  风险等级（可选）
     * @param bounds 边界框，格式为 "minLng,minLat,maxLng,maxLat"（可选）
     * @param limit  返回数量限制（可选）
     * @param offset 偏移量（可选）
     * @return 风险数据列表
     */
    @GetMapping("/data")
    public ApiResponse<List<RiskAssessment>> getRiskData(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String bounds,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer offset) {
        List<RiskAssessment> data = riskService.getRiskData(type, level, bounds, limit, offset);
        return ApiResponse.success(data);
    }

    /**
     * 获取风险统计
     * <p>
     * 获取风险统计数据，包括各等级风险数量、各类型灾害数量等。
     * </p>
     *
     * @param type      风险类型（可选）
     * @param timeRange 时间范围（可选）
     * @return 风险统计数据
     */
    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> getRiskStats(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String timeRange) {
        Map<String, Object> stats = riskService.getRiskStats(type, timeRange);
        return ApiResponse.success(stats);
    }

    /**
     * 获取灾害记录
     * <p>
     * 获取灾害记录列表，支持按类型、时间范围和边界框进行过滤。
     * </p>
     *
     * @param type      灾害类型（可选）
     * @param startTime 开始时间（可选）
     * @param endTime   结束时间（可选）
     * @param bounds    边界框，格式为 "minLng,minLat,maxLng,maxLat"（可选）
     * @param limit     返回数量限制（可选）
     * @param offset    偏移量（可选）
     * @return 灾害记录列表
     */
    @GetMapping("/disasters")
    public ApiResponse<List<DisasterRecord>> getDisasterRecords(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) String bounds,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer offset) {
        List<DisasterRecord> data = riskService.getDisasterRecords(type, startTime, endTime, bounds, limit, offset);
        return ApiResponse.success(data);
    }

    /**
     * 获取预警信息
     * <p>
     * 获取预警信息列表，支持按类型、等级进行过滤。
     * </p>
     *
     * @param type      预警类型（可选）
     * @param level     预警等级（可选）
     * @param timeRange 时间范围（可选）
     * @return 预警信息列表
     */
    @GetMapping("/warnings")
    public ApiResponse<List<Warning>> getWarnings(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String timeRange) {
        List<Warning> data = riskService.getWarnings(type, level, timeRange);
        return ApiResponse.success(data);
    }

    /**
     * 提交承保申请
     * <p>
     * 提交承保申请，系统会自动计算风险评分和费率。
     * </p>
     *
     * @param request 承保申请请求
     * @return 承保标的信息
     */
    @PostMapping("/underwriting")
    public ApiResponse<InsuranceTarget> submitUnderwriting(@Valid @RequestBody UnderwritingRequest request) {
        InsuranceTarget result = riskService.submitUnderwriting(request);
        return ApiResponse.success(result);
    }

    /**
     * 获取承保结果
     * <p>
     * 根据承保标的 ID 获取承保结果。
     * </p>
     *
     * @param id 承保标的 ID
     * @return 承保标的信息
     */
    @GetMapping("/underwriting/{id}")
    public ApiResponse<InsuranceTarget> getUnderwritingResult(@PathVariable Long id) {
        InsuranceTarget result = riskService.getUnderwritingResult(id);
        return ApiResponse.success(result);
    }

    /**
     * 获取理赔列表
     * <p>
     * 获取理赔列表，支持按状态和灾害类型进行过滤。
     * </p>
     *
     * @param status       理赔状态（可选）
     * @param disasterType 灾害类型（可选）
     * @param limit        返回数量限制（可选）
     * @param offset       偏移量（可选）
     * @return 理赔列表
     */
    @GetMapping("/claims")
    public ApiResponse<List<Map<String, Object>>> getClaims(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String disasterType,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer offset) {
        List<Map<String, Object>> data = riskService.getClaims(status, disasterType, limit, offset);
        return ApiResponse.success(data);
    }

    /**
     * 处理赔案
     * <p>
     * 处理赔案申请，包括审核、批准、拒绝等操作。
     * </p>
     *
     * @param id      理赔 ID
     * @param request 处理请求，包含操作类型、备注和金额
     * @return 处理结果
     */
    @PostMapping("/claims/{id}/handle")
    public ApiResponse<Map<String, Object>> handleClaim(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        String action = (String) request.get("action");
        String comment = (String) request.get("comment");
        Double amount = request.get("amount") != null ? ((Number) request.get("amount")).doubleValue() : null;

        Map<String, Object> result = riskService.handleClaim(id, action, comment, amount);
        return ApiResponse.success(result);
    }
}
