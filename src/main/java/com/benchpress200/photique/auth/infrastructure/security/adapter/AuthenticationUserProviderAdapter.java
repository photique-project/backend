package com.benchpress200.photique.auth.infrastructure.security.adapter;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationUserProviderPort;
import com.benchpress200.photique.auth.domain.result.AuthenticationUserResult;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationUserProviderAdapter implements AuthenticationUserProviderPort {
    @Override
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null ||
                authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }

        AuthenticationUserResult authenticationUserResult = (AuthenticationUserResult) authentication.getPrincipal();
        return authenticationUserResult.getUserId();
    }

    @Override
    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        return authentication != null &&
                !(authentication instanceof AnonymousAuthenticationToken);
    }
}
