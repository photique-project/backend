package com.benchpress200.photique.singlework.api.query.controller;

import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.common.api.constant.PathVariableName;
import com.benchpress200.photique.common.api.response.ResponseHandler;
import com.benchpress200.photique.singlework.api.query.constant.SingleWorkQueryResponseMessage;
import com.benchpress200.photique.singlework.api.query.request.SingleWorkCommentsRequest;
import com.benchpress200.photique.singlework.api.query.response.SingleWorkCommentsResponse;
import com.benchpress200.photique.singlework.application.query.model.SingleWorkCommentsQuery;
import com.benchpress200.photique.singlework.application.query.port.in.GetSingleWorkCommentsUseCase;
import com.benchpress200.photique.singlework.application.query.result.SingleWorkCommentsResult;
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
public class SingleWorkCommentQueryController {
    private final GetSingleWorkCommentsUseCase getSingleWorkCommentsUseCase;

    @GetMapping(ApiPath.SINGLEWORK_COMMENT)
    public ResponseEntity<?> getSingleWorkComments(
            @PathVariable(PathVariableName.SINGLEWORK_ID) Long singleWorkId,
            @ModelAttribute @Valid SingleWorkCommentsRequest request
    ) {
        SingleWorkCommentsQuery query = request.toQuery(singleWorkId);
        SingleWorkCommentsResult result = getSingleWorkCommentsUseCase.getSingleWorkComments(query);
        SingleWorkCommentsResponse response = SingleWorkCommentsResponse.from(result);

        return ResponseHandler.handleResponse(
                HttpStatus.OK,
                SingleWorkQueryResponseMessage.COMMENT_FETCH_SUCCESS,
                response
        );
    }
}
