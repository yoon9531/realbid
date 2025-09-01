package com.sy.userservice.util;

import com.sy.userservice.common.FailureStatus;
import com.sy.userservice.domain.User;
import com.sy.userservice.exception.handler.UserHandler;
import com.sy.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SecurityUtil {

    private final UserRepository userRepository;

    public User getCurrentUser () {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            throw new UserHandler(FailureStatus.USER_NOT_AUTHORIZED);
        }

        return userRepository.findByEmail(authentication.getName()).orElseThrow(
                () -> new UserHandler(FailureStatus.USER_NOT_FOUND)
        );
    }
}
