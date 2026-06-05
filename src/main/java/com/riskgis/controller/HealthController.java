package com.riskgis.controller;

import com.riskgis.dto.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查控制器
 * <p>
 * 提供系统健康检查接口，用于监控系统状态。
 * </p>
 */
@RestController
@RequestMapping("/health")
public class HealthController {

    /**
     * 健康检查
     * <p>
     * 检查系统健康状态，返回系统运行状态和时间戳。
     * </p>
     *
     * @return 系统状态信息
     */
    @GetMapping
    public ApiResponse<Map<String, Object>> health() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "UP");
        status.put("timestamp", System.currentTimeMillis());
        return ApiResponse.success(status);
    }
}
