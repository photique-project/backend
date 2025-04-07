package com.benchpress200.photique.user.domain;

import com.benchpress200.photique.user.domain.entity.Follow;
import com.benchpress200.photique.user.domain.entity.User;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FollowDomainService {
    void createFollow(Follow follow);

    void deleteFollow(User follower, User following);

    Page<Follow> getFollowers(User user, Pageable pageable);

    List<Follow> getFollowers(User user);

    Page<Follow> getFollowings(User user, Pageable pageable);

    List<Follow> getFollowings(User user);

    void deleteFollow(User user);

    Long countFollowers(User user);

    Long countFollowings(User user);

    boolean isFollowing(Long followerId, Long followingId);
}
