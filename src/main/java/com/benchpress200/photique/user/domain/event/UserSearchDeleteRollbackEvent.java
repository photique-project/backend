package com.benchpress200.photique.user.domain.event;

import com.benchpress200.photique.user.domain.entity.UserSearch;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserSearchDeleteRollbackEvent {
    private UserSearch userSearch;
}
