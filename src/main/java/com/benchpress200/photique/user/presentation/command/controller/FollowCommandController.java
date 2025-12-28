package com.benchpress200.photique.user.presentation.command.controller;

import com.benchpress200.photique.common.constant.PathVariableName;
import com.benchpress200.photique.common.constant.URL;
import com.benchpress200.photique.common.response.ResponseHandler;
import com.benchpress200.photique.user.application.command.service.FollowCommandService;
import com.benchpress200.photique.user.presentation.command.constant.UserCommandResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(URL.BASE_URL + URL.USER_DOMAIN + URL.USER_DATA + URL.FOLLOW_DOMAIN)
@RequiredArgsConstructor
public class FollowCommandController {
    private final FollowCommandService followCommandService;

    @PostMapping
    public ResponseEntity<?> follow(@PathVariable(PathVariableName.USER_ID) Long followeeId) {
        followCommandService.follow(followeeId);

        return ResponseHandler.handleResponse(
                HttpStatus.CREATED,
                UserCommandResponseMessage.FOLLOWING_COMPLETED
        );
    }

    @DeleteMapping
    public ResponseEntity<?> unfollow(@PathVariable(PathVariableName.USER_ID) Long followeeId) {
        followCommandService.unfollow(followeeId);

        return ResponseHandler.handleResponse(HttpStatus.NO_CONTENT);
    }
}
