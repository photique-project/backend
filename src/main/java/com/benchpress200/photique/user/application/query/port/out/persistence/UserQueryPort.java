package com.benchpress200.photique.user.application.query.port.out.persistence;

import com.benchpress200.photique.user.domain.entity.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserQueryPort {
    Optional<User> findById(Long id);

    Optional<User> findByIdAndDeletedAtIsNull(Long id);

    Optional<User> findByEmailAndDeletedAtIsNull(String email);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Page<User> findByNicknameContaining(String keyword, Pageable pageable);

    Page<User> findByNicknameStartingWithAndDeletedAtIsNull(String keyword, Pageable pageable);

    Page<User> searchByNicknameFts(String keyword, Pageable pageable);
}
