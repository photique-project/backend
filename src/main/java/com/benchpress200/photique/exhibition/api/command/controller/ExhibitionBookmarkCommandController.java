package com.benchpress200.photique.exhibition.api.command.controller;

import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.common.api.constant.PathVariableName;
import com.benchpress200.photique.common.api.response.ResponseHandler;
import com.benchpress200.photique.exhibition.api.command.constant.ExhibitionCommandResponseMessage;
import com.benchpress200.photique.exhibition.application.command.port.in.AddExhibitionBookmarkUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ExhibitionBookmarkCommandController {
    private final AddExhibitionBookmarkUseCase addExhibitionBookmarkUseCase;

    @PostMapping(ApiPath.EXHIBITION_BOOKMARK)
    public ResponseEntity<?> addExhibitionBookmark(
            @PathVariable(PathVariableName.EXHIBITION_ID) Long exhibitionId
    ) {
        addExhibitionBookmarkUseCase.addExhibitionBookmark(exhibitionId);

        return ResponseHandler.handleResponse(
                HttpStatus.CREATED,
                ExhibitionCommandResponseMessage.EXHIBITION_BOOKMARK_SUCCESS
        );
    }
}
