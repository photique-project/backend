package com.benchpress200.photique.user.api.query.controller;

import com.benchpress200.photique.common.constant.PathVariableName;
import com.benchpress200.photique.common.constant.URL;
import com.benchpress200.photique.common.response.ResponseHandler;
import com.benchpress200.photique.user.api.query.constant.UserQueryResponseMessage;
import com.benchpress200.photique.user.api.query.request.NicknameValidateRequest;
import com.benchpress200.photique.user.api.query.request.UserSearchRequest;
import com.benchpress200.photique.user.api.query.response.MyDetailsResponse;
import com.benchpress200.photique.user.api.query.response.NicknameValidateResponse;
import com.benchpress200.photique.user.api.query.response.UserDetailsResponse;
import com.benchpress200.photique.user.api.query.response.UserSearchResponse;
import com.benchpress200.photique.user.application.query.model.NicknameValidateQuery;
import com.benchpress200.photique.user.application.query.model.UserSearchQuery;
import com.benchpress200.photique.user.application.query.port.in.GetMyDetailsUseCase;
import com.benchpress200.photique.user.application.query.port.in.GetUserDetailsUseCase;
import com.benchpress200.photique.user.application.query.port.in.SearchUserUseCase;
import com.benchpress200.photique.user.application.query.port.in.ValidateNicknameUseCase;
import com.benchpress200.photique.user.application.query.result.MyDetailsResult;
import com.benchpress200.photique.user.application.query.result.NicknameValidateResult;
import com.benchpress200.photique.user.application.query.result.UserDetailsResult;
import com.benchpress200.photique.user.application.query.result.UserSearchResult;
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
    private final ValidateNicknameUseCase validateNicknameUseCase;
    private final GetUserDetailsUseCase getUserDetailsUseCase;
    private final GetMyDetailsUseCase getMyDetailsUseCase;
    private final SearchUserUseCase searchUserUseCase;

    @GetMapping(
            path = URL.VALIDATE_NICKNAME,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> validateNickname(
            @ModelAttribute @Valid NicknameValidateRequest request
    ) {
        NicknameValidateQuery query = request.toQuery();
        NicknameValidateResult result = validateNicknameUseCase.validateNickname(query);
        NicknameValidateResponse response = NicknameValidateResponse.from(result);

        return ResponseHandler.handleResponse(
                HttpStatus.OK,
                UserQueryResponseMessage.NICKNAME_VALIDATED,
                response
        );
    }

    @GetMapping(
            path = URL.USER_DATA,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> getUserDetails(@PathVariable(PathVariableName.USER_ID) Long userId) {
        UserDetailsResult result = getUserDetailsUseCase.getUserDetails(userId);
        UserDetailsResponse response = UserDetailsResponse.from(result);

        return ResponseHandler.handleResponse(
                HttpStatus.OK,
                UserQueryResponseMessage.USER_FETCHED,
                response
        );
    }

    @GetMapping(
            path = URL.MY_DATA,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> getMyDetails() {
        MyDetailsResult result = getMyDetailsUseCase.getMyDetails();
        MyDetailsResponse response = MyDetailsResponse.from(result);

        return ResponseHandler.handleResponse(
                HttpStatus.OK,
                UserQueryResponseMessage.MY_DATE_FETCHED,
                response
        );
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> searchUser(
            @ModelAttribute @Valid UserSearchRequest request
    ) {
        UserSearchQuery query = request.toQuery();
        UserSearchResult result = searchUserUseCase.searchUser(query);
        UserSearchResponse response = UserSearchResponse.from(result);

        return ResponseHandler.handleResponse(
                HttpStatus.OK,
                UserQueryResponseMessage.USER_SEARCH_COMPLETED,
                response
        );
    }
}
