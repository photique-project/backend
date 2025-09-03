package com.benchpress200.photique.user.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserSearchSaveRollbackEvent {
    private Long userId;
}
