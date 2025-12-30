package com.benchpress200.photique.user.infrastructure.persistence.jpa;

import com.benchpress200.photique.user.domain.entity.Follow;
import com.benchpress200.photique.user.domain.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    void deleteByFollowerAndFollowee(User follower, User followee);

    Page<Follow> findByFolloweeId(Long followeeId, Pageable pageable);

    List<Follow> findByFolloweeId(Long followeeId);

    Page<Follow> findByFollowerId(Long followerId, Pageable pageable);

    Long countByFollowee(User followee);

    Long countByFollower(User follower);

    Optional<Follow> findByFollowerIdAndFolloweeId(Long followerId, Long followeeId);

    boolean existsByFollowerIdAndFolloweeId(Long followerId, Long followeeId);

    @Query(
            "SELECT f " +
                    "FROM Follow f " +
                    "JOIN FETCH f.follower " +
                    "WHERE f.followee = :followee"
    )
    Slice<Follow> findByFolloweeWithFollower(
            @Param("followee") User followee,
            Pageable pageable
    );

    @Query(
            "SELECT f.followee.id " +
                    "FROM Follow f " +
                    "WHERE f.follower.id = :followerId " +
                    "AND f.followee.id IN :followeeIds"
    )
    Set<Long> findFolloweeIds(
            @Param("followerId") Long followerId,
            @Param("followeeIds") List<Long> followeeIds
    );

    @Query(
            "SELECT u " +
                    "FROM User u " +
                    "JOIN Follow f ON f.follower = u " +
                    "WHERE f.followee.id = :userId " +
                    "AND (:keyword IS NULL OR u.nickname LIKE CONCAT(:keyword, '%'))"
    )
    Page<User> searchFollower(
            @Param("userId") Long userId,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Query(
            "SELECT u " +
                    "FROM User u " +
                    "JOIN Follow f ON f.followee = u " +
                    "WHERE f.follower.id = :userId " +
                    "AND (:keyword IS NULL OR u.nickname LIKE CONCAT(:keyword, '%'))"
    )
    Page<User> searchFollowee(
            @Param("userId") Long userId,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
