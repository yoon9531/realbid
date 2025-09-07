package com.sy.userservice.jwt;

import com.sy.userservice.common.FailureStatus;
import com.sy.userservice.domain.User;
import com.sy.userservice.exception.handler.UserHandler;
import com.sy.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;


import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    private final String testAccessSecret = "dGVzdC1zZWNyZXQta2V5LWZvci11bml0LXRlc3RpbmctcHVycG9zZS13aXRoLXN1ZmZpY2llbnQtbGVuZ3Ro";
    private final String testRefreshSecret = "YW5vdGhlci10ZXN0LXNlY3JldC1rZXktZm9yLXVuaXQtdGVzdGluZy1wdXJwb3NlLXdpdGgtc3VmZmljaWVudC1sZW5ndGg=";

    // 테스트용 만료 시간 (초 단위)
    private final long accessTokenValidity = 3600; // 1시간
    private final long refreshTokenValidity = 86400; // 24시간

    private User testUser;
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(
                testAccessSecret,
                testRefreshSecret,
                accessTokenValidity,
                refreshTokenValidity
        );

        testUser = new User().builder()
                .id(100L)
                .email("test@example.com")
                .nickname("testuser")
                .password("password")
                .build();
    }

    @Test
    @DisplayName("액세스 토큰 생성 및 유효성 검증 성공 (userId 기반)")
    void generateAndValidateAccessToken() {
        // when
        String accessToken = jwtTokenProvider.generateAccessToken(testUser);
        log.info(accessToken);

        // then
        assertNotNull(accessToken);
        assertTrue(jwtTokenProvider.validateAccessToken(accessToken));
        assertEquals(testUser.getId().toString(), jwtTokenProvider.getSubject(accessToken)); // userId 비교
        log.info(jwtTokenProvider.getSubject(accessToken));
    }

    @Test
    @DisplayName("리프레시 토큰 생성 및 유효성 검증 성공")
    void generateAndValidateRefreshToken() {
        // when
        String refreshToken = jwtTokenProvider.generateRefreshToken();

        // then
        assertNotNull(refreshToken);
        assertTrue(jwtTokenProvider.validateRefreshToken(refreshToken));
    }

    @Test
    @DisplayName("만료된 토큰 검증 시 false 반환")
    void validateToken_withExpiredToken() throws InterruptedException {
        // given
        // 만료시간이 1밀리초인 토큰 프로바이더를 새로 생성
        JwtTokenProvider expiredTokenProvider = new JwtTokenProvider(testAccessSecret, testRefreshSecret, 0, 0);
        String expiredToken = expiredTokenProvider.generateAccessToken(testUser);

        // 토큰이 확실히 만료되도록 잠시 대기
        Thread.sleep(10);

        // when
        boolean isValid = jwtTokenProvider.validateAccessToken(expiredToken);

        // then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("잘못된 형식의 토큰 검증 시 false 반환")
    void validateToken_withInvalidToken() {
        // given
        String invalidToken = "this.is.an.invalid.token";

        // when
        boolean isValid = jwtTokenProvider.validateAccessToken(invalidToken);

        // then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("토큰에서 Authentication 객체 생성 성공")
    void getAuthentication() {
        // given
        String accessToken = jwtTokenProvider.generateAccessToken(testUser);

        // when
        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);

        // then
        assertNotNull(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        assertEquals(testUser.getId().toString(), userDetails.getUsername());
        assertTrue(authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_USER")));
    }

    @Test
    @DisplayName("토큰에서 사용자 아이디 추출 성공")
    void getUsername() {
        // given
        String token = jwtTokenProvider.generateAccessToken(testUser);

        // when
        String expectedEmail = "100";
        String actualEmail = jwtTokenProvider.getSubject(token);

        // then
        assertEquals(expectedEmail, actualEmail);
    }
}
