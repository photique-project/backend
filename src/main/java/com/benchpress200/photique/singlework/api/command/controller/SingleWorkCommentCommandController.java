package com.benchpress200.photique.singlework.api.command.controller;

import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.common.api.constant.PathVariableName;
import com.benchpress200.photique.common.api.response.ResponseHandler;
import com.benchpress200.photique.singlework.api.command.constant.SingleWorkCommandResponseMessage;
import com.benchpress200.photique.singlework.api.command.request.SingleWorkCommentCreateRequest;
import com.benchpress200.photique.singlework.api.command.request.SingleWorkCommentUpdateRequest;
import com.benchpress200.photique.singlework.application.command.model.SingleWorkCommentCreateCommand;
import com.benchpress200.photique.singlework.application.command.model.SingleWorkCommentUpdateCommand;
import com.benchpress200.photique.singlework.application.command.port.in.CreateSingleWorkCommentUseCase;
import com.benchpress200.photique.singlework.application.command.port.in.DeleteSingleWorkCommentUseCase;
import com.benchpress200.photique.singlework.application.command.port.in.UpdateSingleWorkCommentUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SingleWorkCommentCommandController {
    private final CreateSingleWorkCommentUseCase createSingleWorkCommentUseCase;
    private final UpdateSingleWorkCommentUseCase updateSingleWorkCommentUseCase;
    private final DeleteSingleWorkCommentUseCase deleteSingleWorkCommentUseCase;

    @PostMapping(ApiPath.SINGLEWORK_COMMENT)
    public ResponseEntity<?> createSingleWorkComment(
            @PathVariable(PathVariableName.SINGLEWORK_ID) Long singleWorkId,
            @RequestBody @Valid SingleWorkCommentCreateRequest request
    ) {
        SingleWorkCommentCreateCommand command = request.toCommand(singleWorkId);
        createSingleWorkCommentUseCase.createSingleWorkComment(command);

        return ResponseHandler.handleResponse(
                HttpStatus.CREATED,
                SingleWorkCommandResponseMessage.COMMENT_CREATE_SUCCESS
        );
    }

    @PatchMapping(ApiPath.SINGLEWORK_COMMENT_DATA)
    public ResponseEntity<?> updateSingleWorkComment(
            @PathVariable(PathVariableName.COMMENT_ID) Long commentId,
            @RequestBody @Valid SingleWorkCommentUpdateRequest request
    ) {
        SingleWorkCommentUpdateCommand command = request.toCommand(commentId);
        updateSingleWorkCommentUseCase.updateSingleWorkComment(command);

        return ResponseHandler.handleResponse(
                HttpStatus.NO_CONTENT
        );
    }

    @DeleteMapping(ApiPath.SINGLEWORK_COMMENT_DATA)
    public ResponseEntity<?> deleteSingleWorkComment(
            @PathVariable(PathVariableName.COMMENT_ID) Long commentId
    ) {
        deleteSingleWorkCommentUseCase.deleteSingleWorkComment(commentId);

        return ResponseHandler.handleResponse(
                HttpStatus.NO_CONTENT
        );
    }
}
