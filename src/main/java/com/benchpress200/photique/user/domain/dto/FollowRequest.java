package com.benchpress200.photique.user.domain.dto;

import com.benchpress200.photique.user.domain.entity.Follow;
import com.benchpress200.photique.user.domain.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FollowRequest {
    private Long followingId;
    private Long followerId;

    public void withFollowerId(final Long followerId) {
        this.followerId = followerId;
    }

    public Follow toEntity(
            final User follower,
            final User following
    ) {
        return Follow.builder()
                .follower(follower)
                .followee(following)
                .build();
    }
}
