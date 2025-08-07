package com.sy.authservice.controller;

import com.sy.authservice.dto.ApiResponse;
import com.sy.authservice.dto.AuthResponseDto;
import com.sy.authservice.dto.LoginRequestDto;
import com.sy.authservice.dto.RegisterRequestDto;
import com.sy.authservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@RequestBody RegisterRequestDto req) {
        authService.register(req);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDto>> login(@RequestBody LoginRequestDto req) {
        return ResponseEntity.ok(ApiResponse.success(authService.login(req)));
    }

    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<Boolean>> validate(@RequestBody String token) {
        return ResponseEntity.ok(ApiResponse.success(authService.validate(token)));
    }

}
