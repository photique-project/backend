package com.benchpress200.photique.exhibition.api.command.controller;

import com.benchpress200.photique.common.constant.ApiPath;
import com.benchpress200.photique.common.constant.MultipartKey;
import com.benchpress200.photique.common.response.ResponseHandler;
import com.benchpress200.photique.exhibition.api.command.constant.ExhibitionCommandResponseMessage;
import com.benchpress200.photique.exhibition.api.command.request.ExhibitionCreateRequest;
import com.benchpress200.photique.exhibition.application.command.model.ExhibitionCreateCommand;
import com.benchpress200.photique.exhibition.application.command.port.in.OpenExhibitionUseCase;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class ExhibitionCommandController {
    private final OpenExhibitionUseCase openExhibitionUseCase;

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
}
