package com.benchpress200.photique.exhibition.api.command.controller;

import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.common.api.constant.MultipartKey;
import com.benchpress200.photique.common.api.constant.PathVariableName;
import com.benchpress200.photique.common.api.response.ResponseHandler;
import com.benchpress200.photique.exhibition.api.command.constant.ExhibitionCommandResponseMessage;
import com.benchpress200.photique.exhibition.api.command.request.ExhibitionCreateRequest;
import com.benchpress200.photique.exhibition.api.command.request.ExhibitionUpdateRequest;
import com.benchpress200.photique.exhibition.application.command.model.ExhibitionCreateCommand;
import com.benchpress200.photique.exhibition.application.command.model.ExhibitionUpdateCommand;
import com.benchpress200.photique.exhibition.application.command.port.in.DeleteExhibitionUseCase;
import com.benchpress200.photique.exhibition.application.command.port.in.OpenExhibitionUseCase;
import com.benchpress200.photique.exhibition.application.command.port.in.UpdateExhibitionDetailsUseCase;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class ExhibitionCommandController {
    private final OpenExhibitionUseCase openExhibitionUseCase;
    private final UpdateExhibitionDetailsUseCase updateExhibitionDetailsUpdate;
    private final DeleteExhibitionUseCase deleteExhibitionUseCase;

    @PostMapping(
            path = ApiPath.EXHIBITION_ROOT,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> openExhibition(
            @RequestPart(MultipartKey.EXHIBITION) @Valid ExhibitionCreateRequest request,
            @RequestPart(value = MultipartKey.IMAGES) List<MultipartFile> images
    ) {
        ExhibitionCreateCommand command = request.toCommand(images);
        openExhibitionUseCase.openExhibition(command);

        return ResponseHandler.handleResponse(
                HttpStatus.CREATED,
                ExhibitionCommandResponseMessage.EXHIBITION_CREATE_SUCCESS
        );
    }

    @PatchMapping(ApiPath.EXHIBITION_DATA)
    public ResponseEntity<?> updateExhibitionDetails(
            @PathVariable(PathVariableName.EXHIBITION_ID) Long exhibitionId,
            @RequestBody @Valid ExhibitionUpdateRequest request
    ) {
        ExhibitionUpdateCommand command = request.toCommand(exhibitionId);
        updateExhibitionDetailsUpdate.updateExhibitionDetailsUpdate(command);

        return ResponseHandler.handleResponse(
                HttpStatus.NO_CONTENT
        );
    }

    @DeleteMapping(ApiPath.EXHIBITION_DATA)
    public ResponseEntity<?> deleteExhibition(
            @PathVariable(PathVariableName.EXHIBITION_ID) Long exhibitionId
    ) {
        deleteExhibitionUseCase.deleteExhibition(exhibitionId);

        return ResponseHandler.handleResponse(
                HttpStatus.NO_CONTENT
        );
    }
}
