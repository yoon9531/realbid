package com.sy.userservice.jwt;

import com.sy.userservice.common.FailureStatus;
import com.sy.userservice.exception.handler.JwtHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.HttpHeaders;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final @Qualifier("handlerExceptionResolver") HandlerExceptionResolver exceptionResolver;
    private final Set<String> whitelist = Set.of(
            "/auth/login",
            "/auth/refresh",
            "/public",
            "/health"
    );

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        // prefix 매칭(하위 path 포함). 필요에 따라 AntPathMatcher 등으로 확장 가능
        String uri = request.getRequestURI();
        return whitelist.stream().anyMatch(uri::startsWith);
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        try {
            // 이미 인증되어 있으면 패스
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                filterChain.doFilter(request, response);
                return;
            }

            // Authorization 헤더에서 토큰 추출
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
                filterChain.doFilter(request, response); // 토큰 없으면 통과(인가 단계에서 차단)
                return;
            }

            // 토큰 추출
            String token = authHeader.substring(BEARER_PREFIX.length());

            // 토큰 검증
            if (!jwtTokenProvider.validateAccessToken(token)) {
                throw new JwtHandler(FailureStatus.INVALID_TOKEN);
            }

            // 토큰에서 사용자 아이디/이메일(subject) 추출 후 유저 로드
            String username = jwtTokenProvider.getUsername(token);
            UserDetails user = userDetailsService.loadUserByUsername(username);

            // 인증 객체 생성 및 컨텍스트 저장
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 필요 시 요청 컨텍스트에 부가 정보 남기기
            request.setAttribute("jwt.subject", username);

            filterChain.doFilter(request, response);

        } catch (Exception ex) {
            exceptionResolver.resolveException(request, response, null, ex);
        }
    }
}