package com.sy.userservice.controller;

import com.sy.userservice.common.ApiResponse;
import com.sy.userservice.dto.LoginRequestDto;
import com.sy.userservice.dto.LoginResponseDto;
import com.sy.userservice.dto.RegisterRequestDto;
import com.sy.userservice.dto.RegisterResponseDto;
import com.sy.userservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<LoginResponseDto> login (@RequestBody LoginRequestDto dto) {
        LoginResponseDto result = authService.login(dto);

        return ApiResponse.success(result);
    }

    @PostMapping("/register")
    public ApiResponse<RegisterResponseDto> register (@Valid @RequestBody RegisterRequestDto dto) {
        RegisterResponseDto response = authService.register(dto);

        return ApiResponse.success(response);
    }
}
