package com.benchpress200.photique.user.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDetailsRequest {
    private Long requestUserId;
    private Long userId;

    public void withUserId(final Long userId) {
        this.userId = userId;

    }
}
