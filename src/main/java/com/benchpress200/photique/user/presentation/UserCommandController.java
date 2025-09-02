package com.benchpress200.photique.user.presentation;

import com.benchpress200.photique.common.response.ResponseHandler;
import com.benchpress200.photique.user.application.UserCommandService;
import com.benchpress200.photique.user.application.command.JoinCommand;
import com.benchpress200.photique.user.application.command.ResetUserPasswordCommand;
import com.benchpress200.photique.user.application.command.UpdateUserDetailsCommand;
import com.benchpress200.photique.user.application.command.UpdateUserPasswordCommand;
import com.benchpress200.photique.user.presentation.constant.ResponseMessage;
import com.benchpress200.photique.user.presentation.request.JoinRequest;
import com.benchpress200.photique.user.presentation.request.ResetUserPasswordRequest;
import com.benchpress200.photique.user.presentation.request.UpdateUserDetailsRequest;
import com.benchpress200.photique.user.presentation.request.UpdateUserPasswordRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class UserCommandController implements UserCommandControllerDocs {
    private final UserCommandService userCommandService;

    @Override
    public ResponseEntity<?> join(
            @RequestPart("user") @Valid final JoinRequest joinRequest,
            @RequestPart(value = "profileImage", required = false) final MultipartFile profileImage
    ) {
        JoinCommand joinCommand = joinRequest.toCommand(profileImage);
        userCommandService.join(joinCommand);

        return ResponseHandler.handleResponse(
                HttpStatus.CREATED,
                ResponseMessage.JOIN_COMPLETED
        );
    }

    @Override
    public ResponseEntity<?> updateUserDetails(
            @PathVariable("userId") final Long userId,
            @RequestPart("user") @Valid final UpdateUserDetailsRequest updateUserDetailsRequest,
            @RequestPart(value = "profileImage", required = false) final MultipartFile profileImage
    ) {
        UpdateUserDetailsCommand updateUserDetailsCommand = updateUserDetailsRequest.toCommand(userId, profileImage);
        userCommandService.updateUserDetails(updateUserDetailsCommand);

        return ResponseHandler.handleResponse(
                HttpStatus.NO_CONTENT,
                ResponseMessage.USER_DETAILS_UPDATED
        );
    }

    @Override
    public ResponseEntity<?> updateUserPassword(
            @PathVariable("userId") final Long userId,
            @RequestBody @Valid final UpdateUserPasswordRequest updateUserPasswordRequest
    ) {
        UpdateUserPasswordCommand updateUserPasswordCommand = updateUserPasswordRequest.toCommand(userId);
        userCommandService.updateUserPassword(updateUserPasswordCommand);

        return ResponseHandler.handleResponse(
                HttpStatus.NO_CONTENT,
                ResponseMessage.USER_PASSWORD_UPDATED
        );
    }

    @Override
    public ResponseEntity<?> resetUserPassword(
            @RequestBody @Valid final ResetUserPasswordRequest resetUserPasswordRequest
    ) {
        ResetUserPasswordCommand resetUserPasswordCommand = resetUserPasswordRequest.toCommand();
        userCommandService.resetUserPassword(resetUserPasswordCommand);

        return ResponseHandler.handleResponse(
                HttpStatus.NO_CONTENT,
                ResponseMessage.USER_PASSWORD_UPDATED
        );
    }

    @Override
    public ResponseEntity<?> withdraw(@PathVariable("userId") final Long userId) {
        userCommandService.withdraw(userId);

        return ResponseHandler.handleResponse(
                HttpStatus.NO_CONTENT,
                ResponseMessage.WITHDRAW_COMPLETED
        );
    }
}
