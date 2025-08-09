package com.sy.authservice.service;

import com.sy.authservice.dto.AuthResponseDto;
import com.sy.authservice.dto.LoginRequestDto;
import com.sy.authservice.dto.RegisterRequestDto;
import com.sy.authservice.entity.User;
import com.sy.authservice.exception.ApiException;
import com.sy.authservice.exception.code.ErrorCode;
import com.sy.authservice.jwt.JwtTokenProvider;
import com.sy.authservice.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    JwtTokenProvider tokenProvider;
    @Mock
    BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    AuthService authService;

    // ---------- register ----------
    @Test
    @DisplayName("register: 신규 이메일이면 저장된다")
    void register_success() {
        // given
        RegisterRequestDto req = new RegisterRequestDto("user@example.com", "rawPw", "nick");
        when(userRepository.findByEmail(req.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode("rawPw")).thenReturn("ENCODED");
        // when
        authService.register(req);
        // then
        verify(userRepository).save(argThat(u ->
                u.getEmail().equals("user@example.com")
                        && u.getPassword().equals("ENCODED")
                        && u.getNickname().equals("nick")
                        && u.getRole().equals("USER")));
    }

    @Test
    @DisplayName("register: 이메일 중복이면 ApiException(EMAIL_DUPLICATION)")
    void register_duplicateEmail_throws() {
        // given
        RegisterRequestDto req = new RegisterRequestDto("dup@example.com", "pw", "nick");
        when(userRepository.findByEmail("dup@example.com"))
                .thenReturn(Optional.of(User.builder().email("dup@example.com").build()));
        // when
        ApiException ex = assertThrows(ApiException.class, () -> authService.register(req));
        // then
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.EMAIL_DUPLICATION);
        verify(userRepository, never()).save(any());
    }

    // ---------- login ----------
    @Test
    @DisplayName("login: 이메일/비밀번호가 일치하면 토큰을 발급하고 응답을 반환")
    void login_success() {
        // given
        LoginRequestDto req = new LoginRequestDto("user@example.com", "rawPw");
        User user = User.builder()
                .email("user@example.com")
                .password("ENCODED")
                .nickname("nick")
                .role("USER")
                .build();

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("rawPw", "ENCODED")).thenReturn(true);
        when(tokenProvider.generateToken(req)).thenReturn("JWT.TOKEN");

        // when
        AuthResponseDto resp = authService.login(req);

        // then
        assertThat(resp).isNotNull();
        assertThat(resp.getEmail()).isEqualTo("user@example.com");
        assertThat(resp.getAccessToken()).isEqualTo("JWT.TOKEN");
        verify(tokenProvider).generateToken(req);
    }

    @Test
    @DisplayName("login: 존재하지 않는 이메일이면 RuntimeException(이메일/비밀번호 불일치)")
    void login_userNotFound_throws() {
        // given
        LoginRequestDto req = new LoginRequestDto("nope@example.com", "pw");
        when(userRepository.findByEmail("nope@example.com")).thenReturn(Optional.empty());

        // when
        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.login(req));

        // then
        assertThat(ex.getMessage()).contains("이메일/비밀번호 불일치");
//        System.out.println(ex.getMessage());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(tokenProvider, never()).generateToken(any());
    }

    @Test
    @DisplayName("login: 비밀번호 불일치면 RuntimeException(이메일/비밀번호 불일치)")
    void login_passwordMismatch_throws() {
        // given
        LoginRequestDto req = new LoginRequestDto("user@example.com", "wrong");
        User user = User.builder().email("user@example.com").password("ENCODED").build();

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "ENCODED")).thenReturn(false);

        // when
        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.login(req));

        // then
        assertThat(ex.getMessage()).contains("이메일/비밀번호 불일치");
        verify(tokenProvider, never()).generateToken(any());
    }

    // ---------- validate ----------
    @Test
    @DisplayName("validate: provider 위임 true")
    void validate_true() {
        when(tokenProvider.validateToken("ok")).thenReturn(true);
        assertThat(authService.validate("ok")).isTrue();
        verify(tokenProvider).validateToken("ok");
    }

    @Test
    @DisplayName("validate: provider 위임 false")
    void validate_false() {
        when(tokenProvider.validateToken("bad")).thenReturn(false);
        assertThat(authService.validate("bad")).isFalse();
        verify(tokenProvider).validateToken("bad");
    }
}