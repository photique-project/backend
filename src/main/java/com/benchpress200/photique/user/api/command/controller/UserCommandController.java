package com.benchpress200.photique.user.api.command.controller;

import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.common.api.constant.MultipartKey;
import com.benchpress200.photique.common.api.constant.PathVariableName;
import com.benchpress200.photique.common.api.response.ResponseHandler;
import com.benchpress200.photique.user.api.command.constant.UserCommandResponseMessage;
import com.benchpress200.photique.user.api.command.request.ResisterRequest;
import com.benchpress200.photique.user.api.command.request.UserDetailsUpdateRequest;
import com.benchpress200.photique.user.api.command.request.UserPasswordResetRequest;
import com.benchpress200.photique.user.api.command.request.UserPasswordUpdateRequest;
import com.benchpress200.photique.user.application.command.model.ResisterCommand;
import com.benchpress200.photique.user.application.command.model.UserDetailsUpdateCommand;
import com.benchpress200.photique.user.application.command.model.UserPasswordResetCommand;
import com.benchpress200.photique.user.application.command.model.UserPasswordUpdateCommand;
import com.benchpress200.photique.user.application.command.port.in.ResetUserPasswordUseCase;
import com.benchpress200.photique.user.application.command.port.in.ResisterUseCase;
import com.benchpress200.photique.user.application.command.port.in.UpdateUserDetailsUseCase;
import com.benchpress200.photique.user.application.command.port.in.UpdateUserPasswordUseCase;
import com.benchpress200.photique.user.application.command.port.in.WithdrawUseCase;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class UserCommandController {
    private final ResisterUseCase resisterUseCase;
    private final UpdateUserDetailsUseCase updateUserDetailsUseCase;
    private final UpdateUserPasswordUseCase updateUserPasswordUseCase;
    private final ResetUserPasswordUseCase resetUserPasswordUseCase;
    private final WithdrawUseCase withdrawUseCase;


    @PostMapping(
            path = ApiPath.USER_ROOT,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> register(
            @RequestPart(MultipartKey.USER) @Valid ResisterRequest request,
            @RequestPart(value = MultipartKey.PROFILE_IMAGE, required = false) MultipartFile profileImage
    ) {
        ResisterCommand command = request.toCommand(profileImage);
        resisterUseCase.resister(command);

        return ResponseHandler.handleResponse(
                HttpStatus.CREATED,
                UserCommandResponseMessage.JOIN_COMPLETED
        );
    }


    @PatchMapping(
            path = ApiPath.USER_DATA,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("authentication.principal.userId.equals(#userId)")
    public ResponseEntity<?> updateUserDetails(
            @PathVariable(PathVariableName.USER_ID) Long userId,
            @RequestPart(MultipartKey.USER) @Valid UserDetailsUpdateRequest request,
            @RequestPart(value = MultipartKey.PROFILE_IMAGE, required = false) MultipartFile profileImage
    ) {
        UserDetailsUpdateCommand command = request.toCommand(userId, profileImage);
        updateUserDetailsUseCase.updateUserDetails(command);

        return ResponseHandler.handleResponse(HttpStatus.NO_CONTENT);
    }


    @PatchMapping(ApiPath.USER_PASSWORD)
    @PreAuthorize("authentication.principal.userId.equals(#userId)")
    public ResponseEntity<?> updateUserPassword(
            @PathVariable(PathVariableName.USER_ID) Long userId,
            @RequestBody @Valid UserPasswordUpdateRequest request
    ) {
        UserPasswordUpdateCommand command = request.toCommand(userId);
        updateUserPasswordUseCase.updateUserPassword(command);

        return ResponseHandler.handleResponse(HttpStatus.NO_CONTENT);
    }


    @PatchMapping(path = ApiPath.USER_PASSWORD_RESET)
    public ResponseEntity<?> resetUserPassword(
            @RequestBody @Valid UserPasswordResetRequest request
    ) {
        UserPasswordResetCommand command = request.toCommand();
        resetUserPasswordUseCase.resetUserPassword(command);

        return ResponseHandler.handleResponse(HttpStatus.NO_CONTENT);
    }


    @DeleteMapping(path = ApiPath.USER_DATA)
    @PreAuthorize("authentication.principal.userId.equals(#userId)")
    public ResponseEntity<?> withdraw(@PathVariable(PathVariableName.USER_ID) Long userId) {
        withdrawUseCase.withdraw(userId);

        return ResponseHandler.handleResponse(HttpStatus.NO_CONTENT);
    }
}
