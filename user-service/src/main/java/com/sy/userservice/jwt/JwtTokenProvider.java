package com.sy.userservice.jwt;
import com.sy.userservice.common.FailureStatus;
import com.sy.userservice.dto.LoginRequestDto;
import com.sy.userservice.exception.handler.JwtHandler;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;
import io.jsonwebtoken.security.Keys;

import static java.nio.charset.StandardCharsets.*;


@Slf4j
@Component
public class JwtTokenProvider {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final Key key;
    private final Long accessTokenValidityInMilliseconds;
    private final JwtParser jwtParser;


    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey,
                            @Value("${jwt.access-expiration}") Long accessTokenValidity) {
        this.key = createSigningKey(secretKey);
        this.accessTokenValidityInMilliseconds = accessTokenValidity * 1000;
        this.jwtParser = Jwts.parserBuilder().setSigningKey(key).build();
    }

    public String generateToken(String email) {

        Long now = (new Date()).getTime();
        Date accessTokenExpiresIn = new Date(now + accessTokenValidityInMilliseconds);

        return Jwts.builder()
                .setSubject(email)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        if (claims.get("auth") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("auth").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public boolean validateToken(String token) {
        try {
            jwtParser.parseClaimsJws(token).getBody();
            return true;
        } catch (ExpiredJwtException e) {
            throw new JwtHandler(FailureStatus.EXPIRED_TOKEN);
        } catch (IllegalArgumentException e) {
            throw new JwtHandler(FailureStatus.EMPTY_TOKEN);
        } catch (JwtException e) {
            handleJwtException(e);
            return false;
        }
    }

    private void handleJwtException(JwtException e) {
        if (e instanceof MalformedJwtException) {
            throw new JwtHandler(FailureStatus.INVALID_TOKEN);
        } else if (e instanceof UnsupportedJwtException) {
            throw new JwtHandler(FailureStatus.UNSUPPORTED_TOKEN);
        } else {
            throw new JwtHandler(FailureStatus.INVALID_TOKEN);
        }
    }


    private Claims parseClaims(String accessToken) {
        try {
            return jwtParser.parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    private Key createSigningKey(String secretKey) {
        byte[] keyBytes = secretKey.getBytes(UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Integer getExpiredTime (String token) {
        Claims claims = parseClaims(token);
        Date expiration = claims.getExpiration();
        Date now = new Date();
        long diff = expiration.getTime() - now.getTime();
        return (int) (diff / 1000);
    }
}