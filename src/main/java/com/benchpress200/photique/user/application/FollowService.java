package com.benchpress200.photique.user.application;

import com.benchpress200.photique.user.domain.dto.FollowRequest;
import com.benchpress200.photique.user.domain.dto.FollowerResponse;
import com.benchpress200.photique.user.domain.dto.FollowingResponse;
import com.benchpress200.photique.user.domain.dto.UnfollowRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FollowService {
    void followUser(FollowRequest followRequest);

    void unfollowUser(UnfollowRequest unfollowRequest);

    Page<FollowerResponse> getFollowers(Long userId, Pageable pageable);

    Page<FollowingResponse> getFollowings(Long userId, Pageable pageable);
}
