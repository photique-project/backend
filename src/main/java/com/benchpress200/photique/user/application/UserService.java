package com.benchpress200.photique.user.application;

import com.benchpress200.photique.user.domain.dto.NicknameValidationRequest;
import com.benchpress200.photique.user.domain.dto.UserDetailsRequest;
import com.benchpress200.photique.user.domain.dto.UserDetailsResponse;
import com.benchpress200.photique.user.domain.dto.UserSearchRequest;
import com.benchpress200.photique.user.domain.dto.UserSearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    void validateNickname(NicknameValidationRequest nicknameValidationRequest);

    UserDetailsResponse getUserDetails(UserDetailsRequest userDetailsRequest);

    Page<UserSearchResponse> searchUsers(UserSearchRequest userSearchRequest, Pageable pageable);
}
