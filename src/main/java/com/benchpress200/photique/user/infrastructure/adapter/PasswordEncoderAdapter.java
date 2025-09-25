package com.benchpress200.photique.user.infrastructure.adapter;

import com.benchpress200.photique.user.domain.port.PasswordEncoderPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordEncoderAdapter implements PasswordEncoderPort {
    private final PasswordEncoder passwordEncoder;

    @Override
    public String encode(final String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(
            final String rawPassword,
            final String encodedPassword
    ) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
