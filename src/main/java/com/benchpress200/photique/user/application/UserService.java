package com.benchpress200.photique.user.application;

import com.benchpress200.photique.user.domain.dto.JoinRequest;
import com.benchpress200.photique.user.domain.dto.UserDetailResponse;
import com.benchpress200.photique.user.domain.dto.UserIdResponse;
import com.benchpress200.photique.user.domain.dto.UserUpdateRequest;

public interface UserService {
    void join(JoinRequest joinRequest);

    UserDetailResponse getUserDetail(Long userId);

    void updateUserDetail(Long userId, UserUpdateRequest userUpdateRequest);

    UserIdResponse getUserId(String accessToken);

    void withdraw(Long userId);
}
