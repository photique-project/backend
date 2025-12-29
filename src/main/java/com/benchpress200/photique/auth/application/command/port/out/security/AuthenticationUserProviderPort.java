package com.benchpress200.photique.auth.application.command.port.out.security;

public interface AuthenticationUserProviderPort {
    Long getCurrentUserId();

    boolean isAuthenticated();
}
