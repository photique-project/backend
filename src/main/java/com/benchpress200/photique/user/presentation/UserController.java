package com.benchpress200.photique.user.presentation;

import com.benchpress200.photique.common.response.ApiSuccessResponse;
import com.benchpress200.photique.common.response.ResponseHandler;
import com.benchpress200.photique.user.application.UserService;
import com.benchpress200.photique.user.domain.dto.JoinRequest;
import com.benchpress200.photique.user.domain.dto.UserInfoResponse;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
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

    @GetMapping("/{userId}")
    public ApiSuccessResponse<?> getUserInfo(
            @PathVariable("userId") Long userId
    ) {
        UserInfoResponse userInfo = userService.getUserInfo(userId);

        return ResponseHandler.handleSuccessResponse(userInfo, HttpStatus.OK);
    }
}
