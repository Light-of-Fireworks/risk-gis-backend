package com.riskgis.controller;

import com.riskgis.dto.request.LoginRequest;
import com.riskgis.dto.request.RegisterRequest;
import com.riskgis.dto.response.ApiResponse;
import com.riskgis.dto.response.LoginResponse;
import com.riskgis.dto.response.UserInfoResponse;
import com.riskgis.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 认证管理控制器
 * <p>
 * 提供用户认证相关的 API 接口，包括登录、注册和获取当前用户信息。
 * </p>
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 用户登录
     * <p>
     * 用户使用用户名和密码进行登录，成功后返回 JWT 令牌。
     * </p>
     *
     * @param request 登录请求，包含用户名和密码
     * @return 登录响应，包含 JWT 令牌、用户名、邮箱和角色列表
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ApiResponse.success(response);
    }

    /**
     * 用户注册
     * <p>
     * 注册新用户，需要提供用户名、密码和邮箱。
     * </p>
     *
     * @param request 注册请求，包含用户名、密码和邮箱
     * @return 用户信息响应
     */
    @PostMapping("/register")
    public ApiResponse<UserInfoResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserInfoResponse response = authService.register(request);
        return ApiResponse.success(response);
    }

    /**
     * 获取当前用户信息
     * <p>
     * 获取当前登录用户的详细信息，包括用户ID、用户名、邮箱、角色等。
     * </p>
     *
     * @param authentication Spring Security 认证对象
     * @return 用户信息响应
     */
    @GetMapping("/me")
    public ApiResponse<UserInfoResponse> getCurrentUser(Authentication authentication) {
        UserInfoResponse response = authService.getCurrentUser(authentication.getName());
        return ApiResponse.success(response);
    }
}
