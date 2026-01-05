package com.benchpress200.photique.exhibition.api.command.controller;

import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.common.api.constant.PathVariableName;
import com.benchpress200.photique.common.api.response.ResponseHandler;
import com.benchpress200.photique.exhibition.api.command.constant.ExhibitionCommandResponseMessage;
import com.benchpress200.photique.exhibition.api.command.request.ExhibitionCommentCreateRequest;
import com.benchpress200.photique.exhibition.application.command.model.ExhibitionCommentCreateCommand;
import com.benchpress200.photique.exhibition.application.command.port.in.CreateExhibitionCommentUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ExhibitionCommentCommandController {
    private final CreateExhibitionCommentUseCase createExhibitionCommentUseCase;

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
}
