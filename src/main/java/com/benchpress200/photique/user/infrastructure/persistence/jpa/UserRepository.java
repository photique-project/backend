package com.benchpress200.photique.user.infrastructure.persistence.jpa;

import com.benchpress200.photique.user.domain.entity.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByIdAndDeletedAtIsNull(Long id);

    Optional<User> findByEmailAndDeletedAtIsNull(String email);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Page<User> findByNicknameContaining(String keyword, Pageable pageable);

    Page<User> findByNicknameStartingWithAndDeletedAtIsNull(String keyword, Pageable pageable);

    @Query(
            value = "SELECT * FROM users " +
                    "WHERE MATCH(nickname) AGAINST (CONCAT('+', ?1) IN BOOLEAN MODE)",
            nativeQuery = true
    )
    Page<User> searchByNicknameFts(String keyword, Pageable pageable);
}
