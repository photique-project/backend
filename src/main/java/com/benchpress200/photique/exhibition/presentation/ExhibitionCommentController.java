package com.benchpress200.photique.exhibition.presentation;

import com.benchpress200.photique.common.constant.ApiPath;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ExhibitionCommentController {

    private final ExhibitionCommentService exhibitionCommentService;

    @PostMapping(ApiPath.EXHIBITION_COMMENT)
    public ApiSuccessResponse<?> createExhibitionComment(
            @PathVariable("exhibitionId") Long exhibitionId,
            @RequestBody @Valid ExhibitionCommentCreateRequest exhibitionCommentCreateRequest
    ) {
        exhibitionCommentCreateRequest.withExhibitionId(exhibitionId);
        exhibitionCommentService.addExhibitionComment(exhibitionCommentCreateRequest);

        return ResponseHandler.handleSuccessResponse(HttpStatus.CREATED);
    }

    @GetMapping(ApiPath.EXHIBITION_COMMENT)
    public ApiSuccessResponse<?> getExhibitionComments(
            @PathVariable("exhibitionId") Long exhibitionId,
            Pageable pageable
    ) {
        Page<ExhibitionCommentDetailResponse> exhibitionCommentPage = exhibitionCommentService.getExhibitionComments(
                exhibitionId, pageable);

        return ResponseHandler.handleSuccessResponse(exhibitionCommentPage, HttpStatus.OK);
    }

    @PatchMapping(ApiPath.EXHIBITION_COMMENT_DATA)
    public ApiSuccessResponse<?> updateExhibitionComment(
            @PathVariable("exhibitionId") Long exhibitionId,
            @PathVariable("commentId") Long commentId,
            @RequestBody @Valid ExhibitionCommentUpdateRequest exhibitionCommentUpdateRequest
    ) {
        exhibitionCommentUpdateRequest.withExhibitionId(exhibitionId);
        exhibitionCommentUpdateRequest.withCommentId(commentId);
        exhibitionCommentService.updateExhibitionComment(exhibitionCommentUpdateRequest);

        return ResponseHandler.handleSuccessResponse(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(ApiPath.EXHIBITION_COMMENT_DATA)
    public ApiSuccessResponse<?> deleteExhibitionComment(
            @PathVariable("exhibitionId") Long exhibitionId,
            @PathVariable("commentId") Long commentId,
            @RequestBody @Valid ExhibitionCommentDeleteRequest exhibitionCommentDeleteRequest
    ) {
        exhibitionCommentDeleteRequest.withExhibitionId(exhibitionId);
        exhibitionCommentDeleteRequest.withCommentId(commentId);
        exhibitionCommentService.deleteExhibitionComment(exhibitionCommentDeleteRequest);

        return ResponseHandler.handleSuccessResponse(HttpStatus.NO_CONTENT);
    }
}
