package com.benchpress200.photique.user.presentation;


import com.benchpress200.photique.common.constant.URL;
import com.benchpress200.photique.common.response.ApiSuccessResponse;
import com.benchpress200.photique.common.response.ResponseHandler;
import com.benchpress200.photique.user.application.FollowService;
import com.benchpress200.photique.user.domain.dto.FollowRequest;
import com.benchpress200.photique.user.domain.dto.FollowerResponse;
import com.benchpress200.photique.user.domain.dto.FollowingResponse;
import com.benchpress200.photique.user.domain.dto.UnfollowRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
            @PathVariable("userId") final Long followerId,
            @RequestBody final FollowRequest followRequest
    ) {
        followRequest.withFollowerId(followerId);
        followService.followUser(followRequest);
        return ResponseHandler.handleSuccessResponse(HttpStatus.CREATED);
    }

    @DeleteMapping(URL.FOLLOW_DOMAIN)
    public ApiSuccessResponse<?> unfollowUser(
            @PathVariable("userId") final Long followerId,
            @RequestBody final UnfollowRequest unfollowRequest

    ) {
        unfollowRequest.withFollowerId(followerId);
        followService.unfollowUser(unfollowRequest);
        return ResponseHandler.handleSuccessResponse(HttpStatus.NO_CONTENT);
    }

    @GetMapping(URL.FOLLOWER)
    public ApiSuccessResponse<?> getFollowers(
            @PathVariable("userId") final Long userId,
            final Pageable pageable
    ) {
        Page<FollowerResponse> followerResponsePage = followService.getFollowers(userId, pageable);
        return ResponseHandler.handleSuccessResponse(followerResponsePage, HttpStatus.OK);
    }

    @GetMapping(URL.FOLLOWING)
    public ApiSuccessResponse<?> getFollowings(
            @PathVariable("userId") final Long userId,
            final Pageable pageable
    ) {
        Page<FollowingResponse> followingResponsePage = followService.getFollowings(userId, pageable);
        return ResponseHandler.handleSuccessResponse(followingResponsePage, HttpStatus.OK);
    }
}
