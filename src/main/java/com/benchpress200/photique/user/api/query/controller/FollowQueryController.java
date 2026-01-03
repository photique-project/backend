package com.benchpress200.photique.user.api.query.controller;

import com.benchpress200.photique.common.constant.ApiPath;
import com.benchpress200.photique.common.constant.PathVariableName;
import com.benchpress200.photique.common.response.ResponseHandler;
import com.benchpress200.photique.user.api.query.constant.FollowQueryResponseMessage;
import com.benchpress200.photique.user.api.query.request.FolloweeSearchRequest;
import com.benchpress200.photique.user.api.query.request.FollowerSearchRequest;
import com.benchpress200.photique.user.api.query.response.FolloweeSearchResponse;
import com.benchpress200.photique.user.api.query.response.FollowerSearchResponse;
import com.benchpress200.photique.user.application.query.model.FolloweeSearchQuery;
import com.benchpress200.photique.user.application.query.model.FollowerSearchQuery;
import com.benchpress200.photique.user.application.query.port.in.SearchFolloweeUseCase;
import com.benchpress200.photique.user.application.query.port.in.SearchFollowerUseCase;
import com.benchpress200.photique.user.application.query.result.FolloweeSearchResult;
import com.benchpress200.photique.user.application.query.result.FollowerSearchResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FollowQueryController {
    private final SearchFollowerUseCase searchFollowerUseCase;
    private final SearchFolloweeUseCase searchFolloweeUseCase;

    @GetMapping(ApiPath.FOLLOWER)
    public ResponseEntity<?> searchFollower(
            @PathVariable(PathVariableName.USER_ID) Long userId,
            @ModelAttribute @Valid FollowerSearchRequest request
    ) {
        FollowerSearchQuery query = request.toQuery(userId);
        FollowerSearchResult result = searchFollowerUseCase.searchFollower(query);
        FollowerSearchResponse response = FollowerSearchResponse.from(result);

        return ResponseHandler.handleResponse(
                HttpStatus.OK,
                FollowQueryResponseMessage.FOLLOWER_SEARCH_COMPLETED,
                response
        );
    }

    @GetMapping(ApiPath.FOLLOWEE)
    public ResponseEntity<?> searchFollowee(
            @PathVariable(PathVariableName.USER_ID) Long userId,
            @ModelAttribute @Valid FolloweeSearchRequest request
    ) {
        FolloweeSearchQuery query = request.toQuery(userId);
        FolloweeSearchResult result = searchFolloweeUseCase.searchFollowee(query);
        FolloweeSearchResponse response = FolloweeSearchResponse.from(result);

        return ResponseHandler.handleResponse(
                HttpStatus.OK,
                FollowQueryResponseMessage.FOLLOWEE_SEARCH_COMPLETED,
                response
        );
    }
}
