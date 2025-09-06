package com.benchpress200.photique.user.presentation;

import com.benchpress200.photique.common.response.ResponseHandler;
import com.benchpress200.photique.user.application.UserQueryService;
import com.benchpress200.photique.user.application.query.ValidateNicknameQuery;
import com.benchpress200.photique.user.application.result.ValidateNicknameResult;
import com.benchpress200.photique.user.presentation.constant.ResponseMessage;
import com.benchpress200.photique.user.presentation.request.ValidateNicknameRequest;
import com.benchpress200.photique.user.presentation.response.ValidateNicknameResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserQueryController implements UserQueryControllerDocs {
    private final UserQueryService userQueryService;
    
    @Override
    public ResponseEntity<?> validateNickname(
            @ModelAttribute @Valid final ValidateNicknameRequest validateNicknameRequest
    ) {
        ValidateNicknameQuery validateNicknameQuery = validateNicknameRequest.toQuery();
        ValidateNicknameResult validateNicknameResult = userQueryService.validateNickname(validateNicknameQuery);
        ValidateNicknameResponse validateNicknameResponse = ValidateNicknameResponse.from(validateNicknameResult);

        return ResponseHandler.handleResponse(
                HttpStatus.OK,
                ResponseMessage.NICKNAME_VALIDATED,
                validateNicknameResponse
        );
    }

//    @GetMapping(URL.USER_DATA)
//    public ApiSuccessResponse<?> getUserDetails(
//            @ModelAttribute final UserDetailsRequest userDetailsRequest,
//            @PathVariable("userId") final Long userId // 팔로우확인을 위한 유저아이디 받아서 넘겨서 응답수정할차례
//    ) {
//        userDetailsRequest.withUserId(userId);
//        UserDetailsResponse userDetail = userService.getUserDetails(userDetailsRequest);
//        return ResponseHandler.handleSuccessResponse(userDetail, HttpStatus.OK);
//    }
//
//    @GetMapping
//    public ApiSuccessResponse<?> searchUsers(
//            @ModelAttribute @Valid final UserSearchRequest userSearchRequest,
//            final Pageable pageable
//    ) {
//        Page<UserSearchResponse> userSearchResponsePage = userService.searchUsers(userSearchRequest, pageable);
//        return ResponseHandler.handleSuccessResponse(userSearchResponsePage, HttpStatus.OK);
//    }
}
