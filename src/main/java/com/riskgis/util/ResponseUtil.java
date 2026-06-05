package com.riskgis.util;

import com.riskgis.dto.response.ApiResponse;
import org.slf4j.MDC;

public class ResponseUtil {

    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = ApiResponse.success(data);
        response.setTraceId(MDC.get("traceId"));
        return response;
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        ApiResponse<T> response = ApiResponse.success(message, data);
        response.setTraceId(MDC.get("traceId"));
        return response;
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        ApiResponse<T> response = ApiResponse.error(code, message);
        response.setTraceId(MDC.get("traceId"));
        return response;
    }
}
