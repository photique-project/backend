package com.benchpress200.photique.user.application;

import com.benchpress200.photique.user.domain.dto.FollowRequest;

public interface FollowService {
    void followUser(FollowRequest followRequest);
}
