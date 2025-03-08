package com.benchpress200.photique.user.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnfollowRequest {
    private Long followerId;
    private Long followingId;

    public void withFollowerId(final Long followerId) {
        this.followerId = followerId;
    }
}
