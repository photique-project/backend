package com.benchpress200.photique.outbox.application.payload;

import com.benchpress200.photique.user.domain.entity.Follow;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FollowPayload {
    private final Long followerId;
    private final Long followeeId;
    private final LocalDateTime createdAt;

    public static FollowPayload from(Follow follow) {
        return FollowPayload.builder()
                .followerId(follow.getFollower().getId())
                .followeeId(follow.getFollowee().getId())
                .createdAt(follow.getCreatedAt())
                .build();
    }
}
