package com.benchpress200.photique.auth.domain.repository;

import com.benchpress200.photique.auth.domain.entity.EmailAuthCode;
import org.springframework.data.repository.CrudRepository;


public interface EmailAuthCodeRepository extends CrudRepository<EmailAuthCode, String> {
}
