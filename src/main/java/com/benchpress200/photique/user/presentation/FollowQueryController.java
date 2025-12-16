package com.benchpress200.photique.user.presentation;

import com.benchpress200.photique.common.constant.PathVariableName;
import com.benchpress200.photique.common.constant.URL;
import com.benchpress200.photique.common.response.ResponseHandler;
import com.benchpress200.photique.user.application.FollowQueryService;
import com.benchpress200.photique.user.application.query.FolloweeSearchQuery;
import com.benchpress200.photique.user.application.query.FollowerSearchQuery;
import com.benchpress200.photique.user.application.result.FolloweeSearchResult;
import com.benchpress200.photique.user.application.result.FollowerSearchResult;
import com.benchpress200.photique.user.presentation.constant.UserResponseMessage;
import com.benchpress200.photique.user.presentation.request.FolloweeSearchRequest;
import com.benchpress200.photique.user.presentation.request.FollowerSearchRequest;
import com.benchpress200.photique.user.presentation.response.FolloweeSearchResponse;
import com.benchpress200.photique.user.presentation.response.FollowerSearchResponse;
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
            @PathVariable(PathVariableName.USER_ID) final Long userId,
            @ModelAttribute @Valid final FollowerSearchRequest followerSearchRequest
    ) {
        FollowerSearchQuery followerSearchQuery = followerSearchRequest.toQuery(userId);
        FollowerSearchResult followerSearchResult = followQueryService.searchFollowers(followerSearchQuery);
        FollowerSearchResponse followerSearchResponse = FollowerSearchResponse.from(followerSearchResult);

        return ResponseHandler.handleResponse(
                HttpStatus.OK,
                UserResponseMessage.FOLLOWER_SEARCH_COMPLETED,
                followerSearchResponse
        );
    }

    @GetMapping(URL.FOLLOWEE)
    public ResponseEntity<?> searchFollowees(
            @PathVariable(PathVariableName.USER_ID) final Long userId,
            @ModelAttribute @Valid final FolloweeSearchRequest followeeSearchRequest
    ) {
        FolloweeSearchQuery followeeSearchQuery = followeeSearchRequest.toQuery(userId);
        FolloweeSearchResult followeeSearchResult = followQueryService.searchFollowees(followeeSearchQuery);
        FolloweeSearchResponse followeeSearchResponse = FolloweeSearchResponse.from(followeeSearchResult);

        return ResponseHandler.handleResponse(
                HttpStatus.OK,
                UserResponseMessage.FOLLOWEE_SEARCH_COMPLETED,
                followeeSearchResponse
        );
    }
}
