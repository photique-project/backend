package com.benchpress200.photique.auth.api.command.controller;

import com.benchpress200.photique.auth.api.command.constant.AuthCommandResponseMessage;
import com.benchpress200.photique.auth.api.command.request.AuthMailCodeValidateRequest;
import com.benchpress200.photique.auth.api.command.request.AuthMailRequest;
import com.benchpress200.photique.auth.api.command.response.AuthCodeValidateResponse;
import com.benchpress200.photique.auth.api.command.response.AuthTokenRefreshResponse;
import com.benchpress200.photique.auth.application.command.model.AuthMailCodeValidateCommand;
import com.benchpress200.photique.auth.application.command.model.AuthMailCommand;
import com.benchpress200.photique.auth.application.command.model.AuthTokenRefreshCommand;
import com.benchpress200.photique.auth.application.command.port.in.RefreshAuthTokenUseCase;
import com.benchpress200.photique.auth.application.command.port.in.SendJoinAuthMailUseCase;
import com.benchpress200.photique.auth.application.command.port.in.SendPasswordAuthMailUseCase;
import com.benchpress200.photique.auth.application.command.port.in.ValidateAuthMailCodeUseCase;
import com.benchpress200.photique.auth.application.command.result.AuthMailCodeValidateResult;
import com.benchpress200.photique.auth.application.command.result.AuthTokenResult;
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
    private final SendJoinAuthMailUseCase sendJoinAuthMailUseCase;
    private final SendPasswordAuthMailUseCase sendPasswordAuthMailUseCase;
    private final ValidateAuthMailCodeUseCase validateAuthMailCodeUseCase;
    private final RefreshAuthTokenUseCase refreshAuthTokenUseCase;

    @PostMapping(URL.JOIN_MAIL)
    public ResponseEntity<?> sendJoinAuthMail(
            @RequestBody @Valid AuthMailRequest request
    ) {
        AuthMailCommand command = request.toCommand();
        sendJoinAuthMailUseCase.sendJoinAuthMail(command);

        return ResponseHandler.handleResponse(
                HttpStatus.CREATED,
                AuthCommandResponseMessage.AUTH_MAIL_SEND_COMPLETED
        );
    }

    @PostMapping(URL.PASSWORD_MAIL)
    public ResponseEntity<?> sendPasswordAuthMail(
            @RequestBody @Valid AuthMailRequest request
    ) {
        AuthMailCommand command = request.toCommand();
        sendPasswordAuthMailUseCase.sendPasswordAuthMail(command);

        return ResponseHandler.handleResponse(
                HttpStatus.CREATED,
                AuthCommandResponseMessage.AUTH_MAIL_SEND_COMPLETED
        );
    }

    @PostMapping(URL.VALIDATE_CODE)
    public ResponseEntity<?> validateAuthMailCode(
            @RequestBody @Valid AuthMailCodeValidateRequest request
    ) {
        AuthMailCodeValidateCommand command = request.toCommand();
        AuthMailCodeValidateResult result = validateAuthMailCodeUseCase.validateAuthMailCode(
                command);
        AuthCodeValidateResponse response = AuthCodeValidateResponse.from(result);

        return ResponseHandler.handleResponse(
                HttpStatus.OK,
                AuthCommandResponseMessage.AUTH_MAIL_CODE_VALIDATION_COMPLETED,
                response
        );
    }

    @PostMapping(URL.REFRESH_TOKEN)
    public ResponseEntity<?> refreshAuthToken(
            @CookieValue(value = "refreshToken") String refreshToken
    ) {
        AuthTokenRefreshCommand command = AuthTokenRefreshCommand.of(refreshToken);
        AuthTokenResult result = refreshAuthTokenUseCase.refreshAuthToken(command);
        AuthTokenRefreshResponse response = AuthTokenRefreshResponse.from(result);

        return ResponseHandler.handleResponse(
                HttpStatus.OK,
                AuthCommandResponseMessage.AUTHENTICATION_TOKEN_REFRESH_COMPLETED,
                response.getAccessTokenResponse(),
                response.getCookie()
        );
    }
}
