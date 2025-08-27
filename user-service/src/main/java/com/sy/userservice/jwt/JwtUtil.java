package com.sy.userservice.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtTokenProvider provider;

    public String createAccessToken (String email) {
        return provider.generateToken(email);
    }

    public String createRefreshToken (String email) {
        return provider.generateToken(email);
    }

    public Integer getTokenExpiredTime (String token) {
        return provider.getExpiredTime(token);
    }
}
