package com.sy.authservice.jwt;

import com.sy.authservice.dto.LoginRequestDto;
import com.sy.authservice.exception.code.ErrorCode;
import com.sy.authservice.exception.handler.JwtHandler;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    private final String secretKey = "realbid-jwt-secret-key-realbid-jwt-secret-key-realbid-jwt-secret-key-realbid-jwt-secret-key-realbid-jwt-secret-key";
    private static final Long EXPIRATION_TIME = 3600000L;

    private JwtTokenProvider provider;
    private Key key;

    @Mock
    private LoginRequestDto loginRequestDto;

    @BeforeEach
    void setUp() {
        provider = new JwtTokenProvider(secretKey, EXPIRATION_TIME);
        key = createSigningKey(secretKey);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("[generateToken] : 유저 email과 만료 시간이 들어간 토큰 생성")
    void generateToken() {
        // given
        when(loginRequestDto.getEmail()).thenReturn("user@example.com");

        // when
        String token = provider.generateToken(loginRequestDto);

        // then
        assertThat(token).isNotEmpty(); // 토큰 반환이 빈 string으로 되지 않았는 지 확인
        // 토큰에 이메일과 만료 시간이 잘 들어갔는 지 확인
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();

        assertThat(claims.getSubject()).isEqualTo(loginRequestDto.getEmail());
//        System.out.println(claims.getSubject());
//        System.out.println(claims.getExpiration());
        assertThat(claims.getExpiration()).isAfter(new Date());

    }

//    @Test
//    @DisplayName("[getAuthentication]: auth 클레임이 없으면 예외를 던진다")
//    void getAuthentication_withoutAuthClaim_throws() {
//        // given
//        String token = buildToken("user@example.com", null, 60);
//
//        // when
//        Executable exec = () -> provider.getAuthentication(token);
//
//        // then
//        RuntimeException ex = assertThrows(RuntimeException.class, exec);
//        assertThat(ex.getMessage()).contains("권한 정보가 없는 토큰");
//    }

    @Test
    @DisplayName("validateToken: 정상 토큰이면 true")
    void validateToken_valid() {
        // given
        String token = buildToken("user@example.com", "ROLE_USER", 60);

        // when & then
        assertThat(provider.validateToken(token)).isTrue();
    }

    @Test
    @DisplayName("validateToken: 만료 토큰이면 AUTH_TOKEN_EXPIRED 예외")
    void validateToken_expired() {
        // given (이미 만료된 토큰: exp -10초)
        String token = buildToken("user@example.com", "ROLE_USER", -10);

        // when
        Executable exec = () -> provider.validateToken(token);

        // then
        JwtHandler ex = assertThrows(JwtHandler.class, exec);
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.AUTH_TOKEN_EXPIRED);
    }

    @Test
    @DisplayName("validateToken: 빈 문자열이면 AUTH_TOKEN_EMPTY 예외")
    void validateToken_empty() {
        Executable exec = () -> provider.validateToken("");
        JwtHandler ex = assertThrows(JwtHandler.class, exec);
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.AUTH_TOKEN_EMPTY);
    }

    @Test
    @DisplayName("validateToken: 포맷이 깨진 토큰이면 INVALID_TOKEN 예외")
    void validateToken_malformed() {
        Executable exec = () -> provider.validateToken("this.is.not.jwt");
        JwtHandler ex = assertThrows(JwtHandler.class, exec);
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_TOKEN);
    }

    // --- helpers ---

    private static Key createSigningKey(String secret) {
        String base64Secret = Base64.getEncoder().encodeToString(secret.getBytes());
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(base64Secret));
    }

    /**
     * 테스트용 JWT 생성
     * @param subject 이메일
     * @param authCsv "ROLE_USER,ROLE_ADMIN" 등. null이면 auth 미포함
     * @param expInSeconds 만료까지 남은 시간(초). 음수면 이미 만료로 생성
     */
    private String buildToken(String subject, String authCsv, int expInSeconds) {
        long now = System.currentTimeMillis();
        Date exp = new Date(now + expInSeconds * 1000L);

        var builder = Jwts.builder()
                .setSubject(subject)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256);

        if (authCsv != null) {
            builder.claim("auth", authCsv);
        }
        return builder.compact();
    }
}
