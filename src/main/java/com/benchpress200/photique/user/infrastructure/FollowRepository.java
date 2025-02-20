package com.benchpress200.photique.user.infrastructure;

import com.benchpress200.photique.user.domain.entity.Follow;
import com.benchpress200.photique.user.domain.entity.User;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    void deleteByFollowerAndFollowing(User Follower, User Following);

    Page<Follow> findByFollowingId(Long followingId, Pageable pageable);

    List<Follow> findByFollowingId(Long followingId);

    Page<Follow> findByFollowerId(Long followerId, Pageable pageable);

    void deleteByFollowerOrFollowing(User Follower, User Following);
}
