package com.benchpress200.photique.user.presentation.query.controller;

import com.benchpress200.photique.common.constant.PathVariableName;
import com.benchpress200.photique.common.constant.URL;
import com.benchpress200.photique.common.response.ResponseHandler;
import com.benchpress200.photique.user.application.query.model.FolloweeSearchQuery;
import com.benchpress200.photique.user.application.query.model.FollowerSearchQuery;
import com.benchpress200.photique.user.application.query.result.FolloweeSearchResult;
import com.benchpress200.photique.user.application.query.result.FollowerSearchResult;
import com.benchpress200.photique.user.application.query.service.FollowQueryService;
import com.benchpress200.photique.user.presentation.query.constant.UserQueryResponseMessage;
import com.benchpress200.photique.user.presentation.query.dto.request.FolloweeSearchRequest;
import com.benchpress200.photique.user.presentation.query.dto.request.FollowerSearchRequest;
import com.benchpress200.photique.user.presentation.query.dto.response.FolloweeSearchResponse;
import com.benchpress200.photique.user.presentation.query.dto.response.FollowerSearchResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(URL.BASE_URL + URL.USER_DOMAIN + URL.USER_DATA + URL.FOLLOW_DOMAIN)
@RequiredArgsConstructor
public class FollowQueryController {
    private final FollowQueryService followQueryService;

    @GetMapping(URL.FOLLOWER)
    public ResponseEntity<?> searchFollowers(
            @PathVariable(PathVariableName.USER_ID) Long userId,
            @ModelAttribute @Valid FollowerSearchRequest followerSearchRequest
    ) {
        FollowerSearchQuery followerSearchQuery = followerSearchRequest.toQuery(userId);
        FollowerSearchResult followerSearchResult = followQueryService.searchFollower(followerSearchQuery);
        FollowerSearchResponse followerSearchResponse = FollowerSearchResponse.from(followerSearchResult);

        return ResponseHandler.handleResponse(
                HttpStatus.OK,
                UserQueryResponseMessage.FOLLOWER_SEARCH_COMPLETED,
                followerSearchResponse
        );
    }

    @GetMapping(URL.FOLLOWEE)
    public ResponseEntity<?> searchFollowees(
            @PathVariable(PathVariableName.USER_ID) Long userId,
            @ModelAttribute @Valid FolloweeSearchRequest followeeSearchRequest
    ) {
        FolloweeSearchQuery followeeSearchQuery = followeeSearchRequest.toQuery(userId);
        FolloweeSearchResult followeeSearchResult = followQueryService.searchFollowee(followeeSearchQuery);
        FolloweeSearchResponse followeeSearchResponse = FolloweeSearchResponse.from(followeeSearchResult);

        return ResponseHandler.handleResponse(
                HttpStatus.OK,
                UserQueryResponseMessage.FOLLOWEE_SEARCH_COMPLETED,
                followeeSearchResponse
        );
    }
}
