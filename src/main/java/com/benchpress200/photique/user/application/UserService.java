package com.benchpress200.photique.user.application;

import com.benchpress200.photique.user.domain.dto.JoinRequest;
import com.benchpress200.photique.user.domain.dto.NicknameValidationRequest;
import com.benchpress200.photique.user.domain.dto.UserDetailResponse;
import com.benchpress200.photique.user.domain.dto.UserIdResponse;
import com.benchpress200.photique.user.domain.dto.UserSearchRequest;
import com.benchpress200.photique.user.domain.dto.UserSearchResponse;
import com.benchpress200.photique.user.domain.dto.UserUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    void validateNickname(NicknameValidationRequest nicknameValidationRequest);

    void join(JoinRequest joinRequest);

    UserDetailResponse getUserDetail(Long userId);

    void updateUserDetail(UserUpdateRequest userUpdateRequest);

    UserIdResponse getUserId(String accessToken);

    void withdraw(Long userId);

    Page<UserSearchResponse> searchUsers(UserSearchRequest userSearchRequest, Pageable pageable);
}
