package com.benchpress200.photique.auth.domain.port.security;

public interface AuthenticationUserProviderPort {
    Long getCurrentUserId();

    boolean isAuthenticated();
}
