package com.riskgis.controller;

import com.riskgis.dto.request.InsurancePolicyQueryRequest;
import com.riskgis.dto.request.InsurancePolicyRegionQueryRequest;
import com.riskgis.dto.response.ApiResponse;
import com.riskgis.dto.response.InsurancePolicyGroupVO;
import com.riskgis.service.InsurancePolicyService;
import com.riskgis.service.InsuranceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/insurance")
public class InsuranceController {

    private final InsuranceService insuranceService;
    private final InsurancePolicyService insurancePolicyService;

    public InsuranceController(InsuranceService insuranceService, InsurancePolicyService insurancePolicyService) {
        this.insuranceService = insuranceService;
        this.insurancePolicyService = insurancePolicyService;
    }

    @GetMapping("/categories")
    public ApiResponse<List<Map<String, Object>>> getCategories() {
        return ApiResponse.success(insuranceService.getAllCategories());
    }

    @GetMapping("/types")
    public ApiResponse<List<Map<String, Object>>> getTypes(@RequestParam String categoryCode) {
        return ApiResponse.success(insuranceService.getTypesByCategory(categoryCode));
    }

    @GetMapping("/tree")
    public ApiResponse<List<Map<String, Object>>> getTree() {
        return ApiResponse.success(insuranceService.getTree());
    }

    @PostMapping("/policy/query")
    public ApiResponse<List<InsurancePolicyGroupVO>> queryPolicies(@RequestBody InsurancePolicyQueryRequest request) {
        return ApiResponse.success(insurancePolicyService.queryByBuffer(request));
    }

    @PostMapping("/policy/query-region")
    public ApiResponse<List<InsurancePolicyGroupVO>> queryPoliciesByRegion(@RequestBody InsurancePolicyRegionQueryRequest request) {
        return ApiResponse.success(insurancePolicyService.queryByRegion(request));
    }
}
