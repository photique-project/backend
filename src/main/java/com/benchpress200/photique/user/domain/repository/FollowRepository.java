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
    void deleteByFollowerAndFollowee(User Follower, User Followee);

    Page<Follow> findByFolloweeId(Long followeeId, Pageable pageable);

    List<Follow> findByFolloweeId(Long followeeId);

    Page<Follow> findByFollowerId(Long followerId, Pageable pageable);


    Long countByFollowee(User followee);

    Long countByFollower(User follower);

    boolean existsByFollowerIdAndFolloweeId(Long followerId, Long followeeId);


    @Query("SELECT f.followee.id " +
            "FROM Follow f " +
            "WHERE f.follower.id = :followerId " +
            "AND f.followee.id IN :followeeIds")
    Set<Long> findFolloweeIds(
            @Param("followerId") Long followerId,
            @Param("followeeIds") List<Long> followeeIds
    );
}
