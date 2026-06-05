package com.riskgis.controller;

import com.riskgis.dto.request.AreaAssessRequest;
import com.riskgis.dto.request.PointAssessRequest;
import com.riskgis.dto.response.ApiResponse;
import com.riskgis.dto.response.RiskAssessResponse;
import com.riskgis.service.RiskAssessService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/risk/assess")
public class RiskAssessController {

    private final RiskAssessService riskAssessService;

    public RiskAssessController(RiskAssessService riskAssessService) {
        this.riskAssessService = riskAssessService;
    }

    @PostMapping("/point")
    public ApiResponse<RiskAssessResponse> assessPoint(@Valid @RequestBody PointAssessRequest request) {
        return ApiResponse.success(riskAssessService.assessPoint(request));
    }

    @PostMapping("/area")
    public ApiResponse<RiskAssessResponse> assessArea(@Valid @RequestBody AreaAssessRequest request) {
        return ApiResponse.success(riskAssessService.assessArea(request));
    }

    @GetMapping("/region/{regionCode}")
    public ApiResponse<RiskAssessResponse> assessRegion(
            @PathVariable String regionCode,
            @RequestParam(required = false) List<String> disasterTypes) {
        return ApiResponse.success(riskAssessService.assessRegion(regionCode, disasterTypes));
    }
}
