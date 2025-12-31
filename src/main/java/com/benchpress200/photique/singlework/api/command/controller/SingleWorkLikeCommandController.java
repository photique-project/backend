package com.benchpress200.photique.singlework.api.command.controller;

import com.benchpress200.photique.common.constant.PathVariableName;
import com.benchpress200.photique.common.constant.URL;
import com.benchpress200.photique.common.response.ResponseHandler;
import com.benchpress200.photique.singlework.api.command.constant.SingleWorkCommandResponseMessage;
import com.benchpress200.photique.singlework.application.command.port.in.AddSingleWorkLikeUseCase;
import com.benchpress200.photique.singlework.application.command.port.in.CancelSingleWorkLikeUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(URL.BASE_URL + URL.SINGLE_WORK_DOMAIN + URL.SINGLE_WORK_DATA + URL.LIKE)
public class SingleWorkLikeCommandController {
    private final AddSingleWorkLikeUseCase addSingleWorkLikeUseCase;
    private final CancelSingleWorkLikeUseCase cancelSingleWorkLikeUseCase;

    @PostMapping
    public ResponseEntity<?> addSingleWorkLike(
            @PathVariable(PathVariableName.SINGLEWORK_ID) Long singleWorkId
    ) {
        addSingleWorkLikeUseCase.addSingleWorkLike(singleWorkId);

        return ResponseHandler.handleResponse(
                HttpStatus.CREATED,
                SingleWorkCommandResponseMessage.WORK_LIKE_SUCCESS
        );
    }

    @DeleteMapping
    public ResponseEntity<?> cancelSingleWorkLike(
            @PathVariable(PathVariableName.SINGLEWORK_ID) Long singleWorkId
    ) {
        cancelSingleWorkLikeUseCase.cancelSingleWorkLike(singleWorkId);

        return ResponseHandler.handleResponse(
                HttpStatus.NO_CONTENT
        );
    }
}
