package com.benchpress200.photique.auth.infrastructure.adapter;

import com.benchpress200.photique.auth.domain.port.AuthenticationUserProviderPort;
import com.benchpress200.photique.auth.domain.result.AuthenticationUserResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationUserProviderAdapter implements AuthenticationUserProviderPort {
    @Override
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthenticationUserResult authenticationUserResult = (AuthenticationUserResult) authentication.getPrincipal();
        return authenticationUserResult.getUserId();
    }
}
