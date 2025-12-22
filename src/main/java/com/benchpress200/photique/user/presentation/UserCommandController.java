package com.benchpress200.photique.user.presentation;

import com.benchpress200.photique.common.constant.MultipartKey;
import com.benchpress200.photique.common.constant.PathVariableName;
import com.benchpress200.photique.common.constant.URL;
import com.benchpress200.photique.common.response.ResponseHandler;
import com.benchpress200.photique.user.application.UserCommandService;
import com.benchpress200.photique.user.application.command.JoinCommand;
import com.benchpress200.photique.user.application.command.ResetUserPasswordCommand;
import com.benchpress200.photique.user.application.command.UpdateUserDetailsCommand;
import com.benchpress200.photique.user.application.command.UpdateUserPasswordCommand;
import com.benchpress200.photique.user.presentation.constant.UserResponseMessage;
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
            @RequestPart(MultipartKey.USER) @Valid JoinRequest joinRequest,
            @RequestPart(value = MultipartKey.PROFILE_IMAGE, required = false) MultipartFile profileImage
    ) {
        JoinCommand joinCommand = joinRequest.toCommand(profileImage);
        userCommandService.join(joinCommand);

        return ResponseHandler.handleResponse(
                HttpStatus.CREATED,
                UserResponseMessage.JOIN_COMPLETED
        );
    }


    @PatchMapping(
            path = URL.USER_DATA,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("authentication.principal.userId.equals(#userId)")
    public ResponseEntity<?> updateUserDetails(
            @PathVariable(PathVariableName.USER_ID) Long userId,
            @RequestPart(MultipartKey.USER) @Valid UpdateUserDetailsRequest updateUserDetailsRequest,
            @RequestPart(value = MultipartKey.PROFILE_IMAGE, required = false) MultipartFile profileImage
    ) {
        UpdateUserDetailsCommand updateUserDetailsCommand = updateUserDetailsRequest.toCommand(userId, profileImage);
        userCommandService.updateUserDetails(updateUserDetailsCommand);

        return ResponseHandler.handleResponse(HttpStatus.NO_CONTENT);
    }


    @PatchMapping(
            path = URL.USER_DATA + URL.PASSWORD,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("authentication.principal.userId.equals(#userId)")
    public ResponseEntity<?> updateUserPassword(
            @PathVariable(PathVariableName.USER_ID) Long userId,
            @RequestBody @Valid UpdateUserPasswordRequest updateUserPasswordRequest
    ) {
        UpdateUserPasswordCommand updateUserPasswordCommand = updateUserPasswordRequest.toCommand(userId);
        userCommandService.updateUserPassword(updateUserPasswordCommand);

        return ResponseHandler.handleResponse(HttpStatus.NO_CONTENT);
    }


    @PatchMapping(
            path = URL.PASSWORD,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> resetUserPassword(
            @RequestBody @Valid ResetUserPasswordRequest resetUserPasswordRequest
    ) {
        ResetUserPasswordCommand resetUserPasswordCommand = resetUserPasswordRequest.toCommand();
        userCommandService.resetUserPassword(resetUserPasswordCommand);

        return ResponseHandler.handleResponse(HttpStatus.NO_CONTENT);
    }


    @DeleteMapping(
            path = URL.USER_DATA,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("authentication.principal.userId.equals(#userId)")
    public ResponseEntity<?> withdraw(@PathVariable(PathVariableName.USER_ID) Long userId) {
        userCommandService.withdraw(userId);

        return ResponseHandler.handleResponse(HttpStatus.NO_CONTENT);
    }
}
