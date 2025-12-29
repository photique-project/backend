package com.benchpress200.photique.user.application.command.port.out.security;

public interface PasswordEncoderPort {
    String encode(String rawPassword);

    boolean matches(String rawPassword, String encodedPassword);
}
