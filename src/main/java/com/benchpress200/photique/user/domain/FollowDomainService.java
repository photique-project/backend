package com.benchpress200.photique.user.domain;

import com.benchpress200.photique.user.domain.entity.Follow;
import com.benchpress200.photique.user.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FollowDomainService {
    void createFollow(Follow follow);

    void deleteFollow(User follower, User following);

    Page<Follow> getFollowers(User user, Pageable pageable);

    Page<Follow> getFollowings(User user, Pageable pageable);

    void deleteFollow(User user);
}
