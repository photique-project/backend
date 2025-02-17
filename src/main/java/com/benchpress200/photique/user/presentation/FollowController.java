package com.benchpress200.photique.user.presentation;


import com.benchpress200.photique.common.constant.URL;
import com.benchpress200.photique.common.response.ApiSuccessResponse;
import com.benchpress200.photique.common.response.ResponseHandler;
import com.benchpress200.photique.user.application.FollowService;
import com.benchpress200.photique.user.domain.dto.FollowRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(URL.BASE_URL + URL.USER_DOMAIN + URL.USER_DATA)
@RequiredArgsConstructor
public class FollowController {
    private final FollowService followService;

    @PostMapping(URL.FOLLOW_DOMAIN)
    public ApiSuccessResponse<?> followUser(
            @PathVariable("userId") final Long userId,
            @RequestBody final FollowRequest followRequest
    ) {
        followRequest.withFollowerId(userId);
        followService.followUser(followRequest);
        return ResponseHandler.handleSuccessResponse(HttpStatus.CREATED);
    }
}
