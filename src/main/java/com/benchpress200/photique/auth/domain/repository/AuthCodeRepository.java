package com.benchpress200.photique.auth.domain.repository;

import com.benchpress200.photique.auth.domain.entity.AuthCode;
import org.springframework.data.repository.CrudRepository;


public interface AuthCodeRepository extends CrudRepository<AuthCode, String> {
}
