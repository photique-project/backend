package com.benchpress200.photique.user.domain.repository;

import com.benchpress200.photique.user.domain.entity.Follow;
import com.benchpress200.photique.user.domain.entity.User;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    void deleteByFollowerAndFollowing(User Follower, User Following);

    Page<Follow> findByFollowingId(Long followingId, Pageable pageable);

    List<Follow> findByFollowingId(Long followingId);

    Page<Follow> findByFollowerId(Long followerId, Pageable pageable);

    void deleteByFollowerOrFollowing(User follower, User following);

    Long countByFollowing(User following);

    Long countByFollower(User follower);

    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);

    List<Follow> findByFollower(User follower);

    @Query("SELECT f.following.id " +
            "FROM Follow f " +
            "WHERE f.follower.id = :currentUserId " +
            "AND f.following.id IN :targetUserIds")
    Set<Long> findFollowingIds(
            @Param("currentUserId") Long currentUserId,
            @Param("targetUserIds") List<Long> targetUserIds
    );
}
