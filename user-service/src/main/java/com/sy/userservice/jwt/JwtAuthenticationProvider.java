package com.sy.userservice.jwt;

import com.sy.userservice.common.FailureStatus;
import com.sy.userservice.domain.User;
import com.sy.userservice.exception.handler.UserHandler;
import com.sy.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String token = (String) authentication.getCredentials();
        if (!jwtTokenProvider.validateAccessToken(token)) {
            throw new BadCredentialsException("Invalid JWT");
        }
        User foundUser = userRepository.findById(Long.valueOf(jwtTokenProvider.getSubject(token)))
                .orElseThrow(() -> new UserHandler(FailureStatus.USER_NOT_FOUND));
        String username = foundUser.getEmail();
        UserDetails user = userDetailsService.loadUserByUsername(username);

        JwtAuthenticationToken result = new JwtAuthenticationToken(token);
        result.setPrincipal(user);
        result.setAuthenticated(true);
        result.setDetails(authentication.getDetails());
        return result;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
