package com.benchpress200.photique.user.application;

import com.benchpress200.photique.user.domain.dto.JoinRequest;
import com.benchpress200.photique.user.domain.dto.UserInfoResponse;

public interface UserService {
    void join(JoinRequest joinRequest);
    UserInfoResponse getUserInfo(Long userId);
}
