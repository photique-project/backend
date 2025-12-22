package com.benchpress200.photique.singlework.presentation;

import com.benchpress200.photique.common.constant.MultipartKey;
import com.benchpress200.photique.common.constant.URL;
import com.benchpress200.photique.common.response.ResponseHandler;
import com.benchpress200.photique.singlework.application.SingleWorkCommandService;
import com.benchpress200.photique.singlework.application.command.NewSingleWorkCommand;
import com.benchpress200.photique.singlework.presentation.constant.SingleWorkResponseMessage;
import com.benchpress200.photique.singlework.presentation.request.NewSingleWorkRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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
            @RequestPart(MultipartKey.SINGLEWORK) @Valid NewSingleWorkRequest newSingleWorkRequest,
            @RequestPart(MultipartKey.IMAGE) MultipartFile image
    ) {
        NewSingleWorkCommand newSingleWorkCommand = newSingleWorkRequest.toCommand(image);
        singleWorkCommandService.postSingleWork(newSingleWorkCommand);

        return ResponseHandler.handleResponse(
                HttpStatus.CREATED,
                SingleWorkResponseMessage.WORK_CREATE_SUCCESS
        );
    }
}
