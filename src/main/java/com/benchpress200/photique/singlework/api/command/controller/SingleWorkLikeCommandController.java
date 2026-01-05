package com.benchpress200.photique.singlework.api.command.controller;

import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.common.api.constant.PathVariableName;
import com.benchpress200.photique.common.api.response.ResponseHandler;
import com.benchpress200.photique.singlework.api.command.constant.SingleWorkCommandResponseMessage;
import com.benchpress200.photique.singlework.application.command.port.in.AddSingleWorkLikeUseCase;
import com.benchpress200.photique.singlework.application.command.port.in.CancelSingleWorkLikeUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SingleWorkLikeCommandController {
    private final AddSingleWorkLikeUseCase addSingleWorkLikeUseCase;
    private final CancelSingleWorkLikeUseCase cancelSingleWorkLikeUseCase;

    @PostMapping(ApiPath.SINGLEWORK_LIKE)
    public ResponseEntity<?> addSingleWorkLike(
            @PathVariable(PathVariableName.SINGLEWORK_ID) Long singleWorkId
    ) {
        addSingleWorkLikeUseCase.addSingleWorkLike(singleWorkId);

        return ResponseHandler.handleResponse(
                HttpStatus.CREATED,
                SingleWorkCommandResponseMessage.WORK_LIKE_SUCCESS
        );
    }

    @DeleteMapping(ApiPath.SINGLEWORK_LIKE)
    public ResponseEntity<?> cancelSingleWorkLike(
            @PathVariable(PathVariableName.SINGLEWORK_ID) Long singleWorkId
    ) {
        cancelSingleWorkLikeUseCase.cancelSingleWorkLike(singleWorkId);

        return ResponseHandler.handleResponse(
                HttpStatus.NO_CONTENT
        );
    }
}
