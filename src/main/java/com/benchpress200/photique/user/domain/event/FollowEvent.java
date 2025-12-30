package com.benchpress200.photique.user.domain.event;

import lombok.Getter;

@Getter
public class FollowEvent {
    private Long followerId;
    private Long followeeId;

    private FollowEvent(
            Long followerId,
            Long followeeId
    ) {
        this.followerId = followerId;
        this.followeeId = followeeId;
    }

    public static FollowEvent of(
            Long followerId,
            Long followeeId
    ) {
        return new FollowEvent(followerId, followeeId);
    }
}
