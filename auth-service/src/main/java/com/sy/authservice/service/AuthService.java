package com.sy.authservice.service;

import com.sy.authservice.dto.AuthResponseDto;
import com.sy.authservice.dto.LoginRequestDto;
import com.sy.authservice.dto.RegisterRequestDto;
import com.sy.authservice.entity.User;
import com.sy.authservice.repository.UserRepository;
import com.sy.authservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    public void register(RegisterRequestDto req) {
        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
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
        Map<String, Object> claims = Map.of(
                "email", user.getEmail(),
                "nickname", user.getNickname(),
                "role", user.getRole()
        );
        String token = jwtUtil.generateToken(user.getId().toString(), claims);
        return new AuthResponseDto(token);
    }

    public boolean validate(String token) {
        return jwtUtil.validateToken(token);
    }

    public String getSubject(String token) {
        return jwtUtil.extractSubject(token);
    }

}
