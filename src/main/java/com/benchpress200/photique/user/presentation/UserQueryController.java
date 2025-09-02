package com.benchpress200.photique.user.presentation;

import com.benchpress200.photique.common.response.ApiSuccessResponse;
import com.benchpress200.photique.common.response.ResponseHandler;
import com.benchpress200.photique.user.application.UserService;
import com.benchpress200.photique.user.presentation.request.ValidateNicknameRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserQueryController implements UserQueryControllerDocs {
    private final UserService userService;

    @Override
    public ApiSuccessResponse<?> validateNickname(
            @ModelAttribute @Valid final ValidateNicknameRequest validateNicknameRequest
    ) {
        // TODO: 409응답은 실제 리소스 생성, 수정 시 충돌나면 응답하는게 적절하고, 실제 중복검사는 200으로 주고 결과를 바디에 담는 것이 좋을듯
        return ResponseHandler.handleSuccessResponse(HttpStatus.OK);
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
