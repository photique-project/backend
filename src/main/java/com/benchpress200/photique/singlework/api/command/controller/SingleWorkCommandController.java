package com.benchpress200.photique.singlework.api.command.controller;

import com.benchpress200.photique.common.constant.MultipartKey;
import com.benchpress200.photique.common.constant.PathVariableName;
import com.benchpress200.photique.common.constant.URL;
import com.benchpress200.photique.common.response.ResponseHandler;
import com.benchpress200.photique.singlework.api.command.constant.SingleWorkCommandResponseMessage;
import com.benchpress200.photique.singlework.api.command.request.SingleWorkCreateRequest;
import com.benchpress200.photique.singlework.api.command.request.SingleWorkUpdateRequest;
import com.benchpress200.photique.singlework.application.command.model.SingleWorkCreateCommand;
import com.benchpress200.photique.singlework.application.command.model.SingleWorkUpdateCommand;
import com.benchpress200.photique.singlework.application.command.port.in.DeleteSingleWorkUseCase;
import com.benchpress200.photique.singlework.application.command.port.in.PostSingleWorkUseCase;
import com.benchpress200.photique.singlework.application.command.port.in.UpdateSingleWorkDetailsUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(URL.BASE_URL + URL.SINGLE_WORK_DOMAIN)
@RequiredArgsConstructor
public class SingleWorkCommandController {
    private final PostSingleWorkUseCase postSingleWorkUseCase;
    private final UpdateSingleWorkDetailsUseCase updateSingleWorkDetailsUseCase;
    private final DeleteSingleWorkUseCase deleteSingleWorkUseCase;

    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> postSingleWork(
            @RequestPart(MultipartKey.SINGLEWORK) @Valid SingleWorkCreateRequest request,
            @RequestPart(MultipartKey.IMAGE) MultipartFile image
    ) {
        SingleWorkCreateCommand command = request.toCommand(image);
        postSingleWorkUseCase.postSingleWork(command);

        return ResponseHandler.handleResponse(
                HttpStatus.CREATED,
                SingleWorkCommandResponseMessage.WORK_CREATE_SUCCESS
        );
    }

    @PatchMapping(URL.SINGLE_WORK_DATA)
    public ResponseEntity<?> updateSingleWorkDetails(
            @PathVariable(PathVariableName.SINGLEWORK_ID) Long singleWorkId,
            @RequestBody SingleWorkUpdateRequest request
    ) {
        SingleWorkUpdateCommand command = request.toCommand(singleWorkId);
        updateSingleWorkDetailsUseCase.updateSingleWorkDetails(command);

        return ResponseHandler.handleResponse(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(URL.SINGLE_WORK_DATA)
    public ResponseEntity<?> deleteSingleWork(
            @PathVariable(PathVariableName.SINGLEWORK_ID) Long singleWorkId
    ) {
        deleteSingleWorkUseCase.deleteSingleWork(singleWorkId);

        return ResponseHandler.handleResponse(HttpStatus.NO_CONTENT);
    }
}
