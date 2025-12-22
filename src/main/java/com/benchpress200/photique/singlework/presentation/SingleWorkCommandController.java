package com.benchpress200.photique.singlework.presentation;

import com.benchpress200.photique.common.constant.MultipartKey;
import com.benchpress200.photique.common.constant.PathVariableName;
import com.benchpress200.photique.common.constant.URL;
import com.benchpress200.photique.common.response.ResponseHandler;
import com.benchpress200.photique.singlework.application.SingleWorkCommandService;
import com.benchpress200.photique.singlework.application.command.CreateSingleWorkCommand;
import com.benchpress200.photique.singlework.application.command.UpdateSingleWorkCommand;
import com.benchpress200.photique.singlework.presentation.constant.SingleWorkResponseMessage;
import com.benchpress200.photique.singlework.presentation.request.CreateSingleWorkRequest;
import com.benchpress200.photique.singlework.presentation.request.UpdateSingleWorkRequest;
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
    private final SingleWorkCommandService singleWorkCommandService;

    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> postSingleWork(
            @RequestPart(MultipartKey.SINGLEWORK) @Valid CreateSingleWorkRequest createSingleWorkRequest,
            @RequestPart(MultipartKey.IMAGE) MultipartFile image
    ) {
        CreateSingleWorkCommand createSingleWorkCommand = createSingleWorkRequest.toCommand(image);
        singleWorkCommandService.postSingleWork(createSingleWorkCommand);

        return ResponseHandler.handleResponse(
                HttpStatus.CREATED,
                SingleWorkResponseMessage.WORK_CREATE_SUCCESS
        );
    }

    @PatchMapping(URL.SINGLE_WORK_DATA)
    public ResponseEntity<?> updateSingleWorkDetails(
            @PathVariable(PathVariableName.SINGLEWORK_ID) Long singleWorkId,
            @RequestBody UpdateSingleWorkRequest updateSingleWorkRequest
    ) {
        UpdateSingleWorkCommand updateSingleWorkCommand = updateSingleWorkRequest.toCommand(singleWorkId);
        singleWorkCommandService.updateSingleWorkDetails(updateSingleWorkCommand);

        return ResponseHandler.handleResponse(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(URL.SINGLE_WORK_DATA)
    public ResponseEntity<?> removeSingleWork(
            @PathVariable(PathVariableName.SINGLEWORK_ID) Long singleWorkId
    ) {
        singleWorkCommandService.removeSingleWork(singleWorkId);
        
        return ResponseHandler.handleResponse(HttpStatus.NO_CONTENT);
    }
}
