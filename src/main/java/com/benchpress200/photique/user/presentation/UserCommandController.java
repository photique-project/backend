package com.benchpress200.photique.user.presentation;

import com.benchpress200.photique.common.constant.URL;
import com.benchpress200.photique.common.response.ResponseHandler;
import com.benchpress200.photique.user.application.UserCommandService;
import com.benchpress200.photique.user.application.command.JoinCommand;
import com.benchpress200.photique.user.presentation.request.JoinRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
            @RequestPart(value = "profileImage", required = false) final MultipartFile profileImages
    ) {
        JoinCommand joinCommand = joinRequest.toCommand(profileImages);
        userCommandService.join(joinCommand);
        
        return ResponseHandler.handleResponse(
                HttpStatus.CREATED,
                "Join completed"
        );
    }

//    @Auth
//    @OwnResource
//    @PatchMapping(URL.USER_DATA)
//    public ApiSuccessResponse<?> updateUserDetails(
//            @PathVariable("userId") final Long userId,
//            @ModelAttribute @Valid final UserUpdateRequest userUpdateRequest
//    ) {
//        userUpdateRequest.withUserId(userId);
//        userService.updateUserDetails(userUpdateRequest);
//        return ResponseHandler.handleSuccessResponse(HttpStatus.NO_CONTENT);
//    }
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
}
