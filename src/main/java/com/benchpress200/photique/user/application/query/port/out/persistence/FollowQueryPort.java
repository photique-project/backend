package com.benchpress200.photique.user.application.query.port.out.persistence;

import com.benchpress200.photique.user.domain.entity.Follow;
import com.benchpress200.photique.user.domain.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface FollowQueryPort {
    void deleteByFollowerAndFollowee(User follower, User followee);

    Page<Follow> findByFolloweeId(Long followeeId, Pageable pageable);

    List<Follow> findByFolloweeId(Long followeeId);

    Page<Follow> findByFollowerId(Long followerId, Pageable pageable);

    Long countByFollowee(User followee);

    Long countByFollower(User follower);

    Optional<Follow> findByFollowerIdAndFolloweeId(Long followerId, Long followeeId);

    boolean existsByFollowerIdAndFolloweeId(Long followerId, Long followeeId);

    Slice<Follow> findByFolloweeWithFollower(
            User followee,
            Pageable pageable
    );

    Set<Long> findFolloweeIds(
            Long followerId,
            List<Long> followeeIds
    );

    Page<User> searchFollower(
            Long userId,
            String keyword,
            Pageable pageable
    );

    Page<User> searchFollowee(
            Long userId,
            String keyword,
            Pageable pageable
    );
}
