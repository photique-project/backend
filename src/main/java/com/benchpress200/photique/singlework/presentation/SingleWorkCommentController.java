package com.benchpress200.photique.singlework.presentation;

import com.benchpress200.photique.auth.interceptor.Auth;
import com.benchpress200.photique.auth.interceptor.OwnResource;
import com.benchpress200.photique.common.constant.URL;
import com.benchpress200.photique.common.response.ApiSuccessResponse;
import com.benchpress200.photique.common.response.ResponseHandler;
import com.benchpress200.photique.singlework.application.SingleWorkCommentService;
import com.benchpress200.photique.singlework.domain.dto.SingleWorkCommentCreateRequest;
import com.benchpress200.photique.singlework.domain.dto.SingleWorkCommentUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(URL.BASE_URL + URL.SINGLE_WORK_COMMENT_DOMAIN)
@RequiredArgsConstructor
public class SingleWorkCommentController {

    private final SingleWorkCommentService singleWorkCommentService;

    @Auth
    @OwnResource
    @PostMapping
    public ApiSuccessResponse<?> createSingleWorkComment(
            @PathVariable("singleworkId") final Long singleWorkId,
            @RequestBody @Valid final SingleWorkCommentCreateRequest singleWorkCommentCreateRequest
    ) {
        singleWorkCommentCreateRequest.withSingleWorkId(singleWorkId);
        singleWorkCommentService.createSingleWorkComment(singleWorkCommentCreateRequest);

        return ResponseHandler.handleSuccessResponse(HttpStatus.CREATED);
    }

    @Auth
    @OwnResource
    @PatchMapping(URL.SINGLE_WORK_COMMENT_DATA)
    public ApiSuccessResponse<?> updateSingleWorkComment(
            @PathVariable("singleworkId") final Long singleWorkId,
            @PathVariable("commentId") final Long commentId,
            @RequestBody @Valid final SingleWorkCommentUpdateRequest singleWorkCommentUpdateRequest
    ) {
        singleWorkCommentUpdateRequest.withSingleWorkId(singleWorkId);
        singleWorkCommentUpdateRequest.withCommentId(commentId);
        singleWorkCommentService.updateSingleWorkComment(singleWorkCommentUpdateRequest);
        
        return ResponseHandler.handleSuccessResponse(HttpStatus.NO_CONTENT);
    }
}
