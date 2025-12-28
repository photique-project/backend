package com.benchpress200.photique.user.domain.port.persistence;

import com.benchpress200.photique.user.domain.entity.Follow;

public interface FollowCommandPort {
    Follow save(Follow follow);

    void delete(Follow follow);
}
