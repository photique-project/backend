package com.benchpress200.photique.auth.presentation.command.controller;

import com.benchpress200.photique.auth.application.command.model.AuthMailCodeValidateCommand;
import com.benchpress200.photique.auth.application.command.model.AuthMailCommand;
import com.benchpress200.photique.auth.application.command.model.AuthTokenRefreshCommand;
import com.benchpress200.photique.auth.application.command.result.AuthMailCodeValidateResult;
import com.benchpress200.photique.auth.application.command.result.AuthTokenResult;
import com.benchpress200.photique.auth.application.command.service.AuthCommandService;
import com.benchpress200.photique.auth.presentation.command.constant.AuthCommandResponseMessage;
import com.benchpress200.photique.auth.presentation.command.dto.request.AuthMailCodeValidateRequest;
import com.benchpress200.photique.auth.presentation.command.dto.request.AuthMailRequest;
import com.benchpress200.photique.auth.presentation.command.dto.response.AuthTokenRefreshResponse;
import com.benchpress200.photique.common.constant.URL;
import com.benchpress200.photique.common.response.ResponseHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(URL.BASE_URL + URL.AUTH_DOMAIN)
@RequiredArgsConstructor
public class AuthCommandController {
    private final AuthCommandService authCommandService;

    @PostMapping(URL.JOIN_MAIL)
    public ResponseEntity<?> sendJoinAuthMail(
            @RequestBody @Valid AuthMailRequest authMailRequest
    ) {
        AuthMailCommand authMailCommand = authMailRequest.toCommand();
        authCommandService.sendJoinAuthMail(authMailCommand);
        return ResponseHandler.handleResponse(
                HttpStatus.CREATED,
                AuthCommandResponseMessage.AUTH_MAIL_SEND_COMPLETED
        );
    }

    @PostMapping(URL.PASSWORD_MAIL)
    public ResponseEntity<?> sendPasswordAuthMail(
            @RequestBody @Valid AuthMailRequest authMailRequest
    ) {
        AuthMailCommand authMailCommand = authMailRequest.toCommand();
        authCommandService.sendPasswordAuthMail(authMailCommand);
        return ResponseHandler.handleResponse(
                HttpStatus.CREATED,
                AuthCommandResponseMessage.AUTH_MAIL_SEND_COMPLETED
        );
    }

    @PostMapping(URL.VALIDATE_CODE)
    public ResponseEntity<?> validateAuthMailCode(
            @RequestBody @Valid AuthMailCodeValidateRequest authMailCodeValidateRequest
    ) {
        AuthMailCodeValidateCommand authMailCodeValidationCommand = authMailCodeValidateRequest.toCommand();
        AuthMailCodeValidateResult authMailCodeValidationResult = authCommandService.validateAuthMailCode(
                authMailCodeValidationCommand);

        return ResponseHandler.handleResponse(
                HttpStatus.OK,
                AuthCommandResponseMessage.AUTH_MAIL_CODE_VALIDATION_COMPLETED,
                authMailCodeValidationResult
        );
    }

    @PostMapping(URL.REFRESH_TOKEN)
    public ResponseEntity<?> refreshAuthToken(
            @CookieValue(value = "refreshToken") String refreshToken
    ) {
        AuthTokenRefreshCommand authTokenRefreshCommand = AuthTokenRefreshCommand.of(refreshToken);
        AuthTokenResult authTokenResult = authCommandService.refreshAuthToken(authTokenRefreshCommand);
        AuthTokenRefreshResponse authTokenRefreshResponse = AuthTokenRefreshResponse.from(authTokenResult);

        return ResponseHandler.handleResponse(
                HttpStatus.OK,
                AuthCommandResponseMessage.AUTHENTICATION_TOKEN_REFRESH_COMPLETED,
                authTokenRefreshResponse.getAccessTokenResponse(),
                authTokenRefreshResponse.getCookie()
        );
    }
}
