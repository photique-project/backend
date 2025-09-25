package com.benchpress200.photique.auth.domain.repository;


import com.benchpress200.photique.auth.domain.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {
    void deleteByUserId(Long userId);
}
