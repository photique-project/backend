package com.benchpress200.photique.user.domain.port.persistence;

import com.benchpress200.photique.user.domain.entity.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserQueryPort {
    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Page<User> findByNicknameContaining(String keyword, Pageable pageable);

    Page<User> findByNicknameStartingWith(String keyword, Pageable pageable);

    Page<User> searchByNicknameFts(String keyword, Pageable pageable);
}
