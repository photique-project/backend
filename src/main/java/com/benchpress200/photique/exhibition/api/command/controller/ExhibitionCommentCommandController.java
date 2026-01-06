package com.benchpress200.photique.exhibition.api.command.controller;

import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.common.api.constant.PathVariableName;
import com.benchpress200.photique.common.api.response.ResponseHandler;
import com.benchpress200.photique.exhibition.api.command.constant.ExhibitionCommandResponseMessage;
import com.benchpress200.photique.exhibition.api.command.request.ExhibitionCommentCreateRequest;
import com.benchpress200.photique.exhibition.api.command.request.ExhibitionCommentUpdateRequest;
import com.benchpress200.photique.exhibition.application.command.model.ExhibitionCommentCreateCommand;
import com.benchpress200.photique.exhibition.application.command.model.ExhibitionCommentUpdateCommand;
import com.benchpress200.photique.exhibition.application.command.port.in.CreateExhibitionCommentUseCase;
import com.benchpress200.photique.exhibition.application.command.port.in.DeleteExhibitionCommentUseCase;
import com.benchpress200.photique.exhibition.application.command.port.in.UpdateExhibitionCommentUseCase;
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
public class ExhibitionCommentCommandController {
    private final CreateExhibitionCommentUseCase createExhibitionCommentUseCase;
    private final UpdateExhibitionCommentUseCase updateExhibitionCommentUseCase;
    private final DeleteExhibitionCommentUseCase deleteExhibitionCommentUseCase;

    @PostMapping(ApiPath.EXHIBITION_COMMENT)
    public ResponseEntity<?> createExhibitionComment(
            @PathVariable(PathVariableName.EXHIBITION_ID) Long exhibitionId,
            @RequestBody @Valid ExhibitionCommentCreateRequest request
    ) {
        ExhibitionCommentCreateCommand command = request.toCommand(exhibitionId);
        createExhibitionCommentUseCase.createExhibitionComment(command);

        return ResponseHandler.handleResponse(
                HttpStatus.CREATED,
                ExhibitionCommandResponseMessage.COMMENT_CREATE_SUCCESS
        );
    }

    @PatchMapping(ApiPath.EXHIBITION_COMMENT_DATA)
    public ResponseEntity<?> updateExhibitionComment(
            @PathVariable(PathVariableName.COMMENT_ID) Long commentId,
            @RequestBody @Valid ExhibitionCommentUpdateRequest request
    ) {
        ExhibitionCommentUpdateCommand command = request.toCommand(commentId);
        updateExhibitionCommentUseCase.updateExhibitionComment(command);

        return ResponseHandler.handleResponse(
                HttpStatus.NO_CONTENT
        );
    }

    @DeleteMapping(ApiPath.EXHIBITION_COMMENT_DATA)
    public ResponseEntity<?> deleteExhibitionComment(
            @PathVariable(PathVariableName.COMMENT_ID) Long commentId
    ) {
        deleteExhibitionCommentUseCase.deleteExhibitionComment(commentId);

        return ResponseHandler.handleResponse(
                HttpStatus.NO_CONTENT
        );
    }
}
