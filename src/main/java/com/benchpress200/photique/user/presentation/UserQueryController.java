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
import org.springframework.web.bind.annotation.PathVariable;
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

    @Override
    public ResponseEntity<?> getUserDetails(@PathVariable("userId") final Long userId) {
        // TODO: 서비스클래스에서 스프링 시큐리티로 로그인한 유저 아이디 조회
        // 토큰 인증방식 문서작성하고 스프링 시큐리티로 교체한 후 API 리팩토링 진행
        return ResponseHandler.handleResponse(
                HttpStatus.OK,
                "User with id [" + userId + "] found"
        );
    }
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
