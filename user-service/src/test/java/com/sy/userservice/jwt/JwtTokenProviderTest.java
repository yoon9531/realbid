package com.sy.userservice.jwt;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;


import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    private final String testAccessSecret = "dGVzdC1zZWNyZXQta2V5LWZvci11bml0LXRlc3RpbmctcHVycG9zZS13aXRoLXN1ZmZpY2llbnQtbGVuZ3Ro";
    private final String testRefreshSecret = "YW5vdGhlci10ZXN0LXNlY3JldC1rZXktZm9yLXVuaXQtdGVzdGluZy1wdXJwb3NlLXdpdGgtc3VmZmljaWVudC1sZW5ndGg=";

    // 테스트용 만료 시간 (초 단위)
    private final long accessTokenValidity = 3600; // 1시간
    private final long refreshTokenValidity = 86400; // 24시간

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(
                testAccessSecret,
                testRefreshSecret,
                accessTokenValidity,
                refreshTokenValidity
        );
    }

    @Test
    @DisplayName("액세스 토큰 생성 및 유효성 검증 성공")
    void generateAndValidateAccessToken() {
        // given
        String userEmail = "test@example.com";

        // when
        String accessToken = jwtTokenProvider.generateAccessToken(userEmail);
        log.info(accessToken);
        // then
        assertNotNull(accessToken);
        assertTrue(jwtTokenProvider.validateAccessToken(accessToken));
        assertEquals(userEmail, jwtTokenProvider.getUsername(accessToken));
        log.info(jwtTokenProvider.getUsername(accessToken));
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
        String userEmail = "test@example.com";
        String expiredToken = expiredTokenProvider.generateAccessToken(userEmail);

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
        String userEmail = "test@example.com";
        String accessToken = jwtTokenProvider.generateAccessToken(userEmail);

        // when
        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);

        // then
        assertNotNull(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        assertEquals(userEmail, userDetails.getUsername());
        assertTrue(authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_USER")));
    }

    @Test
    @DisplayName("토큰에서 사용자 이메일 추출 성공")
    void getUsername() {
        // given
        String expectedEmail = "user@test.com";
        String token = jwtTokenProvider.generateAccessToken(expectedEmail);

        // when
        String actualEmail = jwtTokenProvider.getUsername(token);

        // then
        assertEquals(expectedEmail, actualEmail);
    }
}
