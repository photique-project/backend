package com.benchpress200.photique.user.presentation.query.controller;

import com.benchpress200.photique.common.constant.PathVariableName;
import com.benchpress200.photique.common.constant.URL;
import com.benchpress200.photique.common.response.ResponseHandler;
import com.benchpress200.photique.user.application.query.model.NicknameValidateQuery;
import com.benchpress200.photique.user.application.query.model.UserSearchQuery;
import com.benchpress200.photique.user.application.query.result.MyDetailsResult;
import com.benchpress200.photique.user.application.query.result.NicknameValidateResult;
import com.benchpress200.photique.user.application.query.result.UserDetailsResult;
import com.benchpress200.photique.user.application.query.result.UserSearchResult;
import com.benchpress200.photique.user.application.query.service.UserQueryService;
import com.benchpress200.photique.user.presentation.query.constant.UserQueryResponseMessage;
import com.benchpress200.photique.user.presentation.query.dto.request.NicknameValidateRequest;
import com.benchpress200.photique.user.presentation.query.dto.request.UserSearchRequest;
import com.benchpress200.photique.user.presentation.query.dto.response.MyDetailsResponse;
import com.benchpress200.photique.user.presentation.query.dto.response.NicknameValidateResponse;
import com.benchpress200.photique.user.presentation.query.dto.response.UserDetailsResponse;
import com.benchpress200.photique.user.presentation.query.dto.response.UserSearchResponse;
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
            @ModelAttribute @Valid NicknameValidateRequest nicknameValidateRequest
    ) {
        NicknameValidateQuery nicknameValidateQuery = nicknameValidateRequest.toQuery();
        NicknameValidateResult validateNicknameResult = userQueryService.validateNickname(nicknameValidateQuery);
        NicknameValidateResponse validateNicknameResponse = NicknameValidateResponse.from(validateNicknameResult);

        return ResponseHandler.handleResponse(
                HttpStatus.OK,
                UserQueryResponseMessage.NICKNAME_VALIDATED,
                validateNicknameResponse
        );
    }

    @GetMapping(
            path = URL.USER_DATA,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> getUserDetails(@PathVariable(PathVariableName.USER_ID) Long userId) {
        UserDetailsResult userDetailsResult = userQueryService.getUserDetails(userId);
        UserDetailsResponse userDetailsResponse = UserDetailsResponse.from(userDetailsResult);

        return ResponseHandler.handleResponse(
                HttpStatus.OK,
                UserQueryResponseMessage.USER_FETCHED,
                userDetailsResponse
        );
    }

    @GetMapping(
            path = URL.MY_DATA,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> getMyDetails() {
        MyDetailsResult myDetailsResult = userQueryService.getMyDetails();
        MyDetailsResponse myDetailsResponse = MyDetailsResponse.from(myDetailsResult);

        return ResponseHandler.handleResponse(
                HttpStatus.OK,
                UserQueryResponseMessage.MY_DATE_FETCHED,
                myDetailsResponse
        );
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> searchUser(
            @ModelAttribute @Valid UserSearchRequest userSearchRequest
    ) {
        UserSearchQuery userSearchQuery = userSearchRequest.toQuery();
        UserSearchResult userSearchResult = userQueryService.searchUser(userSearchQuery);
        UserSearchResponse userSearchResponse = UserSearchResponse.from(userSearchResult);

        return ResponseHandler.handleResponse(
                HttpStatus.OK,
                UserQueryResponseMessage.USER_SEARCH_COMPLETED,
                userSearchResponse
        );
    }
}
