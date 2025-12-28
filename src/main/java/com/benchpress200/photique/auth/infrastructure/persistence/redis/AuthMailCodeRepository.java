package com.benchpress200.photique.auth.infrastructure.persistence.redis;

import com.benchpress200.photique.auth.domain.entity.AuthMailCode;
import org.springframework.data.repository.CrudRepository;


public interface AuthMailCodeRepository extends CrudRepository<AuthMailCode, String> {
}
