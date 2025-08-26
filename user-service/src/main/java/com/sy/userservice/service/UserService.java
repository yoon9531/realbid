package com.sy.userservice.service;

import com.sy.userservice.domain.User;
import com.sy.userservice.dto.LoginRequestDto;
import com.sy.userservice.dto.LoginResponseDto;
import com.sy.userservice.dto.RegisterRequestDto;
import com.sy.userservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public LoginResponseDto login(LoginRequestDto dto) {

    }

    @Transactional
    public void register(RegisterRequestDto dto) {
        // 1. 중복 체크
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        if (userRepository.existsByNickname(dto.getNickname())) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }

        // 2. 비밀번호 해싱
        String encodedPassword = passwordEncoder.encode(dto.getPassword());

        // 3. 저장
        User user = User.builder()
                .email(dto.getEmail())
                .password(encodedPassword)
                .nickname(dto.getNickname())
                .build();

        userRepository.save(user);

    }

    public Boolean validateToken(String token) {
        return true;
    }
}
