package com.benchpress200.photique.user.application;

import com.benchpress200.photique.user.domain.dto.JoinRequest;
import com.benchpress200.photique.user.domain.dto.NicknameValidationRequest;
import com.benchpress200.photique.user.domain.dto.UserDetailRequest;
import com.benchpress200.photique.user.domain.dto.UserDetailResponse;
import com.benchpress200.photique.user.domain.dto.UserSearchRequest;
import com.benchpress200.photique.user.domain.dto.UserSearchResponse;
import com.benchpress200.photique.user.domain.dto.UserUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    void validateNickname(NicknameValidationRequest nicknameValidationRequest);

    void join(JoinRequest joinRequest);

    UserDetailResponse getUserDetail(UserDetailRequest userDetailRequest);

    void updateUserDetail(UserUpdateRequest userUpdateRequest);

    void withdraw(Long userId);

    Page<UserSearchResponse> searchUsers(UserSearchRequest userSearchRequest, Pageable pageable);
}
