package com.benchpress200.photique.auth.infrastructure.persistence.adapter;

import com.benchpress200.photique.auth.domain.entity.AuthMailCode;
import com.benchpress200.photique.auth.domain.port.persistence.AuthMailCodeCommandPort;
import com.benchpress200.photique.auth.infrastructure.persistence.redis.AuthMailCodeRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AuthMailCodeCommandAdapter implements AuthMailCodeCommandPort {
    private final AuthMailCodeRepository authMailCodeRepository;

    @Override
    public Optional<AuthMailCode> findById(String email) {
        return authMailCodeRepository.findById(email);
    }

    @Override
    public AuthMailCode save(AuthMailCode authMailCode) {
        return authMailCodeRepository.save(authMailCode);
    }
}
