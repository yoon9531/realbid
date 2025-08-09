package com.sy.authservice.service;

import com.sy.authservice.dto.AuthResponseDto;
import com.sy.authservice.dto.LoginRequestDto;
import com.sy.authservice.dto.RegisterRequestDto;
import com.sy.authservice.entity.User;
import com.sy.authservice.exception.ApiException;
import com.sy.authservice.exception.code.ErrorCode;
import com.sy.authservice.jwt.JwtTokenProvider;
import com.sy.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;
    private final BCryptPasswordEncoder passwordEncoder;

    public void register(RegisterRequestDto req) {
        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new ApiException(ErrorCode.EMAIL_DUPLICATION);
        }

        User user = User.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .nickname(req.getNickname())
                .role("USER")
                .build();

        userRepository.save(user);
    }

    public AuthResponseDto login(LoginRequestDto req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("이메일/비밀번호 불일치"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("이메일/비밀번호 불일치");
        }

        String token = tokenProvider.generateToken(req);
        return new AuthResponseDto(user.getEmail(), token);
    }

    public boolean validate(String token) {

        return tokenProvider.validateToken(token);
    }
}
