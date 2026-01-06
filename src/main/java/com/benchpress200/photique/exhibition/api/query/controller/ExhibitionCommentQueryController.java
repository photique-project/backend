package com.benchpress200.photique.exhibition.api.query.controller;

import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.common.api.constant.PathVariableName;
import com.benchpress200.photique.common.api.response.ResponseHandler;
import com.benchpress200.photique.exhibition.api.query.constant.ExhibitionQueryResponseMessage;
import com.benchpress200.photique.exhibition.api.query.request.ExhibitionCommentsRequest;
import com.benchpress200.photique.exhibition.api.query.response.ExhibitionCommentsResponse;
import com.benchpress200.photique.exhibition.application.query.model.ExhibitionCommentsQuery;
import com.benchpress200.photique.exhibition.application.query.port.in.GetExhibitionCommentsUseCase;
import com.benchpress200.photique.exhibition.application.query.result.ExhibitionCommentsResult;
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
public class ExhibitionCommentQueryController {
    private final GetExhibitionCommentsUseCase getExhibitionCommentsUseCase;

    @GetMapping(ApiPath.EXHIBITION_COMMENT)
    public ResponseEntity<?> getExhibitionComments(
            @PathVariable(PathVariableName.EXHIBITION_ID) Long exhibitionId,
            @ModelAttribute @Valid ExhibitionCommentsRequest request
    ) {
        ExhibitionCommentsQuery query = request.toQuery(exhibitionId);
        ExhibitionCommentsResult result = getExhibitionCommentsUseCase.getExhibitionComments(query);
        ExhibitionCommentsResponse response = ExhibitionCommentsResponse.from(result);

        return ResponseHandler.handleResponse(
                HttpStatus.OK,
                ExhibitionQueryResponseMessage.COMMENT_FETCH_SUCCESS,
                response
        );
    }
}
