package com.riskgis.controller;

import com.riskgis.dto.response.ApiResponse;
import com.riskgis.service.OrganizationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/organization")
public class OrganizationController {

    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @GetMapping("/tree")
    public ApiResponse<List<Map<String, Object>>> getTree() {
        return ApiResponse.success(organizationService.getTree());
    }
}
