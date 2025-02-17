package com.benchpress200.photique.user.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UnfollowRequest {
    private Long followerId;
    private Long followingId;
}
