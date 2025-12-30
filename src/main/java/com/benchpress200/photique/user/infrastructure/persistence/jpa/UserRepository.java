package com.benchpress200.photique.user.infrastructure.persistence.jpa;

import com.benchpress200.photique.user.domain.entity.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("""
            SELECT u
            FROM User u
            WHERE u.id = :id AND u.deletedAt IS NULL
            """)
    Optional<User> findActiveById(@Param("id") Long id);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Page<User> findByNicknameContaining(String keyword, Pageable pageable);

    Page<User> findByNicknameStartingWith(String keyword, Pageable pageable);

    @Query(
            value = "SELECT * FROM users " +
                    "WHERE MATCH(nickname) AGAINST (CONCAT('+', ?1) IN BOOLEAN MODE)",
            nativeQuery = true
    )
    Page<User> searchByNicknameFts(String keyword, Pageable pageable);
}
