package com.benchpress200.photique.user.application;

import com.benchpress200.photique.user.domain.dto.JoinRequest;
import com.benchpress200.photique.user.domain.dto.UpdateUserRequest;
import com.benchpress200.photique.user.domain.dto.UserIdResponse;
import com.benchpress200.photique.user.domain.dto.UserInfoResponse;

public interface UserService {
    void join(JoinRequest joinRequest);

    UserInfoResponse getUserInfo(Long userId);

    void updateUserInfo(Long userId, UpdateUserRequest updateUserRequest);

    UserIdResponse getUserId(String accessToken);

    void withdraw(Long userId);
}
