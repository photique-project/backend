package com.benchpress200.photique.auth.infrastructure.persistence.adapter;

import com.benchpress200.photique.auth.domain.entity.AuthMailCode;
import com.benchpress200.photique.auth.domain.port.persistence.AuthMailCodeQueryPort;
import com.benchpress200.photique.auth.infrastructure.persistence.redis.AuthMailCodeRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AuthMailCodeQueryAdapter implements AuthMailCodeQueryPort {
    private final AuthMailCodeRepository mailCodeRepository;
    
    @Override
    public Optional<AuthMailCode> findById(String id) {
        return mailCodeRepository.findById(id);
    }
}
