package com.benchpress200.photique.auth.infrastructure.persistence.adapter;

import com.benchpress200.photique.auth.application.command.port.out.persistence.AuthMailCodeCommandPort;
import com.benchpress200.photique.auth.application.query.port.out.persistence.AuthMailCodeQueryPort;
import com.benchpress200.photique.auth.domain.entity.AuthMailCode;
import com.benchpress200.photique.auth.infrastructure.persistence.redis.AuthMailCodeRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AuthMailCodePersistenceAdapter implements
        AuthMailCodeCommandPort,
        AuthMailCodeQueryPort {
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
