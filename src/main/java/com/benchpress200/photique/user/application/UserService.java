package com.benchpress200.photique.user.application;

import com.benchpress200.photique.user.domain.dto.JoinRequest;

public interface UserService {
    void join(JoinRequest joinRequest);
}
