package com.riskgis.service;

import com.riskgis.dto.request.LoginRequest;
import com.riskgis.dto.request.RegisterRequest;
import com.riskgis.dto.response.LoginResponse;
import com.riskgis.dto.response.UserInfoResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    UserInfoResponse register(RegisterRequest request);
    UserInfoResponse getCurrentUser(String username);
}
