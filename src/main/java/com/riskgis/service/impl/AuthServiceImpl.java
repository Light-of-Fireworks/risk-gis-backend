package com.riskgis.service.impl;

import com.riskgis.dto.request.LoginRequest;
import com.riskgis.dto.request.RegisterRequest;
import com.riskgis.dto.response.LoginResponse;
import com.riskgis.dto.response.UserInfoResponse;
import com.riskgis.mapper.RoleMapper;
import com.riskgis.mapper.UserMapper;
import com.riskgis.mapper.UserRoleMapper;
import com.riskgis.model.Role;
import com.riskgis.model.User;
import com.riskgis.model.UserRole;
import com.riskgis.security.JwtTokenProvider;
import com.riskgis.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           UserMapper userMapper,
                           RoleMapper roleMapper,
                           UserRoleMapper userRoleMapper,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.userRoleMapper = userRoleMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userMapper.selectByUsername(request.getUsername());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        List<Role> roles = userMapper.selectRolesByUserId(user.getId());
        List<String> roleNames = roles.stream()
                .map(Role::getName)
                .collect(Collectors.toList());

        String token = jwtTokenProvider.createToken(user.getUsername(), roleNames);

        return new LoginResponse(token, user.getUsername(), user.getEmail(), roleNames);
    }

    @Override
    @Transactional
    public UserInfoResponse register(RegisterRequest request) {
        User existingUser = userMapper.selectByUsername(request.getUsername());
        if (existingUser != null) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setEnabled(true);
        user.setDeleted(false);
        userMapper.insert(user);

        Role userRole = roleMapper.selectByName("ROLE_USER");
        if (userRole == null) {
            throw new RuntimeException("默认角色不存在");
        }

        UserRole userRoleMapping = new UserRole();
        userRoleMapping.setUserId(user.getId());
        userRoleMapping.setRoleId(userRole.getId());
        userRoleMapper.insert(userRoleMapping);

        List<Role> roles = Collections.singletonList(userRole);

        return new UserInfoResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getEnabled(),
                roles.stream().map(Role::getName).collect(Collectors.toList()),
                user.getCreatedAt()
        );
    }

    @Override
    public UserInfoResponse getCurrentUser(String username) {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        List<Role> roles = userMapper.selectRolesByUserId(user.getId());

        return new UserInfoResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getEnabled(),
                roles.stream().map(Role::getName).collect(Collectors.toList()),
                user.getCreatedAt()
        );
    }
}
