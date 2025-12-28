package com.benchpress200.photique.user.domain.port.persistence;

import com.benchpress200.photique.user.domain.entity.User;

public interface UserCommandPort {
    User save(User user);
}
