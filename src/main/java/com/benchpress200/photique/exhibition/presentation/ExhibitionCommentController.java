package com.benchpress200.photique.exhibition.presentation;

import com.benchpress200.photique.auth.interceptor.Auth;
import com.benchpress200.photique.auth.interceptor.OwnResource;
import com.benchpress200.photique.common.constant.URL;
import com.benchpress200.photique.common.response.ApiSuccessResponse;
import com.benchpress200.photique.common.response.ResponseHandler;
import com.benchpress200.photique.exhibition.application.ExhibitionCommentService;
import com.benchpress200.photique.exhibition.domain.dto.ExhibitionCommentCreateRequest;
import com.benchpress200.photique.exhibition.domain.dto.ExhibitionCommentDeleteRequest;
import com.benchpress200.photique.exhibition.domain.dto.ExhibitionCommentDetailResponse;
import com.benchpress200.photique.exhibition.domain.dto.ExhibitionCommentUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(URL.BASE_URL + URL.EXHIBITION_COMMENT_DOMAIN)
@RequiredArgsConstructor
public class ExhibitionCommentController {

    private final ExhibitionCommentService exhibitionCommentService;

    @Auth
    @OwnResource
    @PostMapping
    public ApiSuccessResponse<?> createExhibitionComment(
            @PathVariable("exhibitionId") final Long exhibitionId,
            @RequestBody @Valid final ExhibitionCommentCreateRequest exhibitionCommentCreateRequest
    ) {
        exhibitionCommentCreateRequest.withExhibitionId(exhibitionId);
        exhibitionCommentService.createExhibitionComment(exhibitionCommentCreateRequest);

        return ResponseHandler.handleSuccessResponse(HttpStatus.CREATED);
    }

    @Auth
    @GetMapping
    public ApiSuccessResponse<?> getExhibitionComments(
            @PathVariable("exhibitionId") final Long exhibitionId,
            final Pageable pageable
    ) {
        Page<ExhibitionCommentDetailResponse> exhibitionCommentPage = exhibitionCommentService.getExhibitionComments(
                exhibitionId, pageable);

        return ResponseHandler.handleSuccessResponse(exhibitionCommentPage, HttpStatus.OK);
    }

    @Auth
    @OwnResource
    @PatchMapping(URL.EXHIBITION_COMMENT_DATA)
    public ApiSuccessResponse<?> updateExhibitionComment(
            @PathVariable("exhibitionId") final Long exhibitionId,
            @PathVariable("commentId") final Long commentId,
            @RequestBody @Valid final ExhibitionCommentUpdateRequest exhibitionCommentUpdateRequest
    ) {
        exhibitionCommentUpdateRequest.withExhibitionId(exhibitionId);
        exhibitionCommentUpdateRequest.withCommentId(commentId);
        exhibitionCommentService.updateExhibitionComment(exhibitionCommentUpdateRequest);

        return ResponseHandler.handleSuccessResponse(HttpStatus.NO_CONTENT);
    }

    @Auth
    @OwnResource
    @DeleteMapping(URL.EXHIBITION_COMMENT_DATA)
    public ApiSuccessResponse<?> deleteExhibitionComment(
            @PathVariable("exhibitionId") final Long exhibitionId,
            @PathVariable("commentId") final Long commentId,
            @RequestBody @Valid final ExhibitionCommentDeleteRequest exhibitionCommentDeleteRequest
    ) {
        exhibitionCommentDeleteRequest.withExhibitionId(exhibitionId);
        exhibitionCommentDeleteRequest.withCommentId(commentId);
        exhibitionCommentService.deleteExhibitionComment(exhibitionCommentDeleteRequest);

        return ResponseHandler.handleSuccessResponse(HttpStatus.NO_CONTENT);
    }
}
