package com.sy.userservice.service;

import com.sy.userservice.common.FailureStatus;
import com.sy.userservice.domain.User;
import com.sy.userservice.dto.LoginRequestDto;
import com.sy.userservice.dto.LoginResponseDto;
import com.sy.userservice.dto.RegisterRequestDto;
import com.sy.userservice.dto.RegisterResponseDto;
import com.sy.userservice.exception.handler.UserHandler;
import com.sy.userservice.jwt.JwtTokenProvider;
import com.sy.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional(readOnly = true)
    public LoginResponseDto login(LoginRequestDto dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new UserHandler(FailureStatus.USER_ALREADY_EXIST));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new UserHandler(FailureStatus.INVALID_PASSWORD);
        }

        String access  = jwtTokenProvider.generateAccessToken(user.getEmail());
        String refresh = jwtTokenProvider.generateRefreshToken();

        return new LoginResponseDto(access, refresh, user.getNickname());
    }

    @Transactional
    public RegisterResponseDto register(RegisterRequestDto dto) {
        // 1. 중복 체크
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new UserHandler(FailureStatus.USER_ALREADY_EXIST);
        }
        if (userRepository.existsByNickname(dto.getNickname())) {
            throw new UserHandler(FailureStatus.NICKNAME_ALREADY_EXIST);
        }

        // 2. 비밀번호 해싱
        String encodedPassword = passwordEncoder.encode(dto.getPassword());

        // 3. 저장
        User user = User.builder()
                .email(dto.getEmail())
                .password(encodedPassword)
                .nickname(dto.getNickname())
                .build();

        return new RegisterResponseDto(user.getNickname());
    }

    public Boolean validateToken(String token) {
        return jwtTokenProvider.validateAccessToken(token);
    }

}
