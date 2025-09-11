package com.benchpress200.photique.user.presentation;

import com.benchpress200.photique.common.constant.URL;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequiredArgsConstructor
@RequestMapping(URL.BASE_URL + URL.USER_DOMAIN)
public class UserCommandController {
    private final UserCommandService userCommandService;


    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
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


    @PatchMapping(
            path = URL.USER_DATA,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("#userId == authentication.principal.userId")
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


    @PatchMapping(
            path = URL.USER_DATA + URL.PASSWORD,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("#userId == authentication.principal.userId")
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


    @PatchMapping(
            path = URL.PASSWORD,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
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


    @DeleteMapping(
            path = URL.USER_DATA,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("#userId == authentication.principal.userId")
    public ResponseEntity<?> withdraw(@PathVariable("userId") final Long userId) {
        userCommandService.withdraw(userId);

        return ResponseHandler.handleResponse(
                HttpStatus.NO_CONTENT,
                ResponseMessage.WITHDRAW_COMPLETED
        );
    }
}
