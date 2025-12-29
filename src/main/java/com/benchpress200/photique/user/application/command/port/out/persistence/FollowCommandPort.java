package com.benchpress200.photique.user.application.command.port.out.persistence;

import com.benchpress200.photique.user.domain.entity.Follow;

public interface FollowCommandPort {
    Follow save(Follow follow);

    void delete(Follow follow);
}
