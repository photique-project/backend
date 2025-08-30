package com.benchpress200.photique.user.presentation;

import com.benchpress200.photique.common.constant.URL;
import com.benchpress200.photique.common.response.ResponseHandler;
import com.benchpress200.photique.user.application.UserCommandService;
import com.benchpress200.photique.user.application.command.JoinCommand;
import com.benchpress200.photique.user.application.command.UpdateUserDetailsCommand;
import com.benchpress200.photique.user.application.command.UpdateUserPasswordCommand;
import com.benchpress200.photique.user.presentation.request.JoinRequest;
import com.benchpress200.photique.user.presentation.request.UpdateUserDetailsRequest;
import com.benchpress200.photique.user.presentation.request.UpdateUserPasswordRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "User API", description = "유저 도메인 API 입니다.")
@RestController
@RequestMapping(URL.BASE_URL + URL.USER_DOMAIN)
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
                "Join completed"
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
                "User details updated"
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
                "User password updated"
        );
    }
//
//    @Auth
//    @OwnResource
//    @DeleteMapping(URL.USER_DATA)
//    public ApiSuccessResponse<?> withdraw(
//            @PathVariable("userId") final Long userId
//    ) {
//        userService.withdraw(userId);
//        return ResponseHandler.handleSuccessResponse(HttpStatus.NO_CONTENT);
//    }
//
//    @PatchMapping(URL.PASSWORD)
//    public ApiSuccessResponse<?> resetPassword(
//            @RequestBody final ResetPasswordRequest resetPasswordRequest
//    ) {
//        userService.resetPassword(resetPasswordRequest);
//        return ResponseHandler.handleSuccessResponse(HttpStatus.NO_CONTENT);
//    }
    // TODO: 비밀번호만 업데이트하는 API도 추가
}
