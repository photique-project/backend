package com.benchpress200.photique.exhibition.presentation;

import com.benchpress200.photique.auth.interceptor.Auth;
import com.benchpress200.photique.auth.interceptor.OwnResource;
import com.benchpress200.photique.common.constant.URL;
import com.benchpress200.photique.common.response.ApiSuccessResponse;
import com.benchpress200.photique.common.response.ResponseHandler;
import com.benchpress200.photique.exhibition.application.ExhibitionCommentService;
import com.benchpress200.photique.exhibition.domain.dto.ExhibitionCommentCreateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

}
