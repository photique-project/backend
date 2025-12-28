package com.benchpress200.photique.auth.domain.port.persistence;

import com.benchpress200.photique.auth.domain.entity.AuthMailCode;
import java.util.Optional;

public interface AuthMailCodeQueryPort {
    Optional<AuthMailCode> findById(String id);
}
