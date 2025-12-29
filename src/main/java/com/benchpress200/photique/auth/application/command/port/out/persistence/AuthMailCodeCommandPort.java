package com.benchpress200.photique.auth.application.command.port.out.persistence;

import com.benchpress200.photique.auth.domain.entity.AuthMailCode;

public interface AuthMailCodeCommandPort {
    AuthMailCode save(AuthMailCode authMailCode);
}
