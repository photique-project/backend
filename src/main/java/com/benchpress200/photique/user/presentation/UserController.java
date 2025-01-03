package com.benchpress200.photique.user.presentation;

import com.benchpress200.photique.auth.interceptor.Auth;
import com.benchpress200.photique.auth.interceptor.OwnResource;
import com.benchpress200.photique.common.response.ApiSuccessResponse;
import com.benchpress200.photique.common.response.ResponseHandler;
import com.benchpress200.photique.constant.URL;
import com.benchpress200.photique.user.application.UserService;
import com.benchpress200.photique.user.domain.dto.JoinRequest;
import com.benchpress200.photique.user.domain.dto.UpdateUserRequest;
import com.benchpress200.photique.user.domain.dto.UserIdResponse;
import com.benchpress200.photique.user.domain.dto.UserInfoResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(URL.BASE_URL + URL.USER_DOMAIN)
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ApiSuccessResponse<?> join(
            @ModelAttribute @Valid final JoinRequest joinRequest
    ) {
        userService.join(joinRequest);
        return ResponseHandler.handleSuccessResponse(HttpStatus.CREATED);
    }

    @Auth
    @GetMapping(URL.USER_INFO)
    public ApiSuccessResponse<?> getUserInfo(
            @PathVariable("userId") final Long userId
    ) {
        UserInfoResponse userInfo = userService.getUserInfo(userId);
        return ResponseHandler.handleSuccessResponse(userInfo, HttpStatus.OK);
    }

    @Auth
    @OwnResource
    @PatchMapping(URL.USER_INFO)
    public ApiSuccessResponse<?> updateUserInfo(
            @PathVariable("userId") final Long userId,
            @ModelAttribute @Valid final UpdateUserRequest updateUserRequest
    ) {
        userService.updateUserInfo(userId, updateUserRequest);
        return ResponseHandler.handleSuccessResponse(HttpStatus.NO_CONTENT);
    }

    @Auth
    @GetMapping(URL.GET_USER_ID)
    public ApiSuccessResponse<?> getUserId(
            @CookieValue("Authorization") final String accessToken
    ) {
        UserIdResponse userIdResponse = userService.getUserId(accessToken);
        return ResponseHandler.handleSuccessResponse(userIdResponse, HttpStatus.OK);
    }

    @Auth
    @OwnResource
    @DeleteMapping(URL.USER_INFO)
    public ApiSuccessResponse<?> withdraw(
            @PathVariable("userId") final Long userId
    ) {
        userService.withdraw(userId);
        return ResponseHandler.handleSuccessResponse(HttpStatus.NO_CONTENT);
    }
}
