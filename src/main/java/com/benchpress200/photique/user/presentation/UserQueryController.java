package com.benchpress200.photique.user.presentation;

import com.benchpress200.photique.common.constant.URL;
import com.benchpress200.photique.common.response.ResponseHandler;
import com.benchpress200.photique.user.application.UserQueryService;
import com.benchpress200.photique.user.application.query.ValidateNicknameQuery;
import com.benchpress200.photique.user.application.result.UserDetailsResult;
import com.benchpress200.photique.user.application.result.ValidateNicknameResult;
import com.benchpress200.photique.user.presentation.constant.ResponseMessage;
import com.benchpress200.photique.user.presentation.request.ValidateNicknameRequest;
import com.benchpress200.photique.user.presentation.response.UserDetailsResponse;
import com.benchpress200.photique.user.presentation.response.ValidateNicknameResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(URL.BASE_URL + URL.USER_DOMAIN)
@RequiredArgsConstructor
public class UserQueryController {
    private final UserQueryService userQueryService;

    @GetMapping(
            path = URL.VALIDATE_NICKNAME,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
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

    @GetMapping(
            path = URL.USER_DATA,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> getUserDetails(@PathVariable("userId") final Long userId) {
        UserDetailsResult userDetailsResult = userQueryService.getUserDetails(userId);
        UserDetailsResponse userDetailsResponse = UserDetailsResponse.from(userDetailsResult);

        return ResponseHandler.handleResponse(
                HttpStatus.OK,
                "User with id [" + userId + "] found",
                userDetailsResponse
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
