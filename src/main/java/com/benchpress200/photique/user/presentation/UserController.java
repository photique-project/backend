package com.benchpress200.photique.user.presentation;

import com.benchpress200.photique.common.constant.URL;
import com.benchpress200.photique.common.interceptor.Auth;
import com.benchpress200.photique.common.response.ApiSuccessResponse;
import com.benchpress200.photique.common.response.ResponseHandler;
import com.benchpress200.photique.user.application.UserService;
import com.benchpress200.photique.user.domain.dto.NicknameValidationRequest;
import com.benchpress200.photique.user.domain.dto.UserDetailsRequest;
import com.benchpress200.photique.user.domain.dto.UserDetailsResponse;
import com.benchpress200.photique.user.domain.dto.UserSearchRequest;
import com.benchpress200.photique.user.domain.dto.UserSearchResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User API", description = "유저 도메인 API 입니다.")
@RestController
@RequestMapping(URL.BASE_URL + URL.USER_DOMAIN)
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping(URL.VALIDATE_NICKNAME)
    public ApiSuccessResponse<?> validateNickname(
            @Valid final NicknameValidationRequest nicknameValidationRequest
    ) {
        userService.validateNickname(nicknameValidationRequest);
        return ResponseHandler.handleSuccessResponse(HttpStatus.NO_CONTENT);
    }

    @Auth
    @GetMapping(URL.USER_DATA)
    public ApiSuccessResponse<?> getUserDetails(
            @ModelAttribute final UserDetailsRequest userDetailsRequest,
            @PathVariable("userId") final Long userId // 팔로우확인을 위한 유저아이디 받아서 넘겨서 응답수정할차례
    ) {
        userDetailsRequest.withUserId(userId);
        UserDetailsResponse userDetail = userService.getUserDetails(userDetailsRequest);
        return ResponseHandler.handleSuccessResponse(userDetail, HttpStatus.OK);
    }

    @Auth
    @GetMapping
    public ApiSuccessResponse<?> searchUsers(
            @ModelAttribute @Valid final UserSearchRequest userSearchRequest,
            final Pageable pageable
    ) {
        Page<UserSearchResponse> userSearchResponsePage = userService.searchUsers(userSearchRequest, pageable);
        return ResponseHandler.handleSuccessResponse(userSearchResponsePage, HttpStatus.OK);
    }
}
