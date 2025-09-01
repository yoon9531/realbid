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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Mockito 기능을 JUnit 5 테스트에서 사용
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    // 사용자 등록 성공
    @Test
    @DisplayName("사용자 등록 성공")
    void register_success() {
        // given (준비)
        RegisterRequestDto requestDto = new RegisterRequestDto("test@email.com", "password123", "테스트유저");
        String encodedPassword = "encodedPassword";

        // Mock 객체 행동 정의: 중복 이메일/닉네임 없음, 비밀번호 인코딩 결과 설정
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByNickname(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn(encodedPassword);

        // when (실행)
        RegisterResponseDto responseDto = authService.register(requestDto);

        // then (검증)
        assertNotNull(responseDto);
        assertEquals(requestDto.getNickname(), responseDto.getNickname());
        // 중복 체크와 인코딩 메서드가 각각 1번씩 호출되었는지 확인
        verify(userRepository, times(1)).existsByEmail(requestDto.getEmail());
        verify(userRepository, times(1)).existsByNickname(requestDto.getNickname());
        verify(passwordEncoder, times(1)).encode(requestDto.getPassword());
    }

    // 사용자 등록 실패 - 이메일 중복
    @Test
    @DisplayName("사용자 등록 실패 - 이메일 중복")
    void register_fail_duplicateEmail() {
        // given
        RegisterRequestDto requestDto = new RegisterRequestDto("test@email.com", "password123", "테스트유저");
        when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(true);

        // when & then
        UserHandler exception = assertThrows(UserHandler.class, () -> {
            authService.register(requestDto);
        });

        assertEquals(FailureStatus.USER_ALREADY_EXIST, exception.getStatus());
        verify(userRepository, times(1)).existsByEmail(requestDto.getEmail());
        verify(userRepository, never()).existsByNickname(anyString()); // 닉네임 중복체크는 실행되면 안 됨
        verify(passwordEncoder, never()).encode(anyString()); // 비밀번호 인코딩도 실행되면 안 됨
    }


    // 사용자 로그인 성공 - jwt 정상 반환
    @Test
    @DisplayName("사용자 로그인 성공")
    void login_success() {
        // given
        LoginRequestDto requestDto = new LoginRequestDto("test@email.com", "password123");
        User user = User.builder()
                .email("test@email.com")
                .password("encodedPassword")
                .nickname("테스트유저")
                .build();

        when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(requestDto.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtTokenProvider.generateAccessToken(anyString())).thenReturn("dummyAccessToken");
        when(jwtTokenProvider.generateRefreshToken()).thenReturn("dummyRefreshToken");

        // when
        LoginResponseDto responseDto = authService.login(requestDto);

        // then
        assertNotNull(responseDto);
        assertEquals("dummyAccessToken", responseDto.getAccessToken());
        assertEquals("dummyRefreshToken", responseDto.getRefreshToken());
        assertEquals(user.getNickname(), responseDto.getNickname());
        verify(userRepository, times(1)).findByEmail(requestDto.getEmail());
        verify(passwordEncoder, times(1)).matches(requestDto.getPassword(), user.getPassword());
        verify(jwtTokenProvider, times(1)).generateAccessToken(user.getEmail());
    }


    // 사용자 로그인 실패 - 비밀번호 불일치
    @Test
    @DisplayName("사용자 로그인 실패 - 비밀번호 불일치")
    void login_fail_invalidPassword() {
        // given
        LoginRequestDto requestDto = new LoginRequestDto("test@email.com", "wrongPassword");
        User user = User.builder()
                .email("test@email.com")
                .password("encodedPassword")
                .nickname("테스트유저")
                .build();

        when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(requestDto.getPassword(), user.getPassword())).thenReturn(false);

        // when & then
        UserHandler exception = assertThrows(UserHandler.class, () -> authService.login(requestDto));

        assertEquals(FailureStatus.INVALID_PASSWORD, exception.getStatus());
        verify(jwtTokenProvider, never()).generateAccessToken(anyString()); // 토큰 생성 로직이 호출되지 않았는지 확인
    }

    // 사용자 로그인 실패 - 존재하지 않는 사용자
    @Test
    @DisplayName("사용자 로그인 실패 - 존재하지 않는 사용자")
    void login_fail_userNotFound() {
        // given
        LoginRequestDto requestDto = new LoginRequestDto("notfound@email.com", "password123");

        when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.empty());

        // when & then
        UserHandler exception = assertThrows(UserHandler.class, () -> authService.login(requestDto));

        // 참고: 원본 코드에서는 유저가 없을 때 USER_ALREADY_EXIST를 던지고 있어 그대로 테스트합니다.
        // 일반적으로는 USER_NOT_FOUND 같은 별도의 상태를 사용하는 것이 더 명확할 수 있습니다.
        assertEquals(FailureStatus.USER_ALREADY_EXIST, exception.getStatus());
        verify(passwordEncoder, never()).matches(anyString(), anyString()); // 비밀번호 비교 로직이 호출되지 않았는지 확인
        verify(jwtTokenProvider, never()).generateAccessToken(anyString()); // 토큰 생성 로직이 호출되지 않았는지 확인
    }


    // 토큰 검증
    @Test
    @DisplayName("토큰 검증 성공")
    void validateToken_success() {
        // given
        String token = "valid-token";
        when(jwtTokenProvider.validateAccessToken(token)).thenReturn(true);

        // when
        Boolean isValid = authService.validateToken(token);

        // then
        assertTrue(isValid);
        verify(jwtTokenProvider, times(1)).validateAccessToken(token);
    }
}
