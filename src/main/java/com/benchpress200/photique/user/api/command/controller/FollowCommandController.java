package com.benchpress200.photique.user.api.command.controller;

import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.common.api.constant.PathVariableName;
import com.benchpress200.photique.common.api.response.ResponseHandler;
import com.benchpress200.photique.user.api.command.constant.FollowCommandResponseMessage;
import com.benchpress200.photique.user.application.command.port.in.FollowUseCase;
import com.benchpress200.photique.user.application.command.port.in.UnfollowUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FollowCommandController {
    private final FollowUseCase followUseCase;
    private final UnfollowUseCase unfollowUseCase;

    @PostMapping(ApiPath.FOLLOW_ROOT)
    public ResponseEntity<?> follow(@PathVariable(PathVariableName.USER_ID) Long followeeId) {
        followUseCase.follow(followeeId);

        return ResponseHandler.handleResponse(
                HttpStatus.CREATED,
                FollowCommandResponseMessage.FOLLOWING_COMPLETED
        );
    }

    @DeleteMapping(ApiPath.FOLLOW_ROOT)
    public ResponseEntity<?> unfollow(@PathVariable(PathVariableName.USER_ID) Long followeeId) {
        unfollowUseCase.unfollow(followeeId);

        return ResponseHandler.handleResponse(HttpStatus.NO_CONTENT);
    }
}
