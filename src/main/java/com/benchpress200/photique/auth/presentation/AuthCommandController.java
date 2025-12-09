package com.benchpress200.photique.auth.presentation;

import com.benchpress200.photique.auth.application.AuthCommandService;
import com.benchpress200.photique.auth.application.command.AuthMailCommand;
import com.benchpress200.photique.auth.presentation.constant.ResponseMessage;
import com.benchpress200.photique.auth.presentation.request.AuthMailRequest;
import com.benchpress200.photique.common.constant.URL;
import com.benchpress200.photique.common.response.ResponseHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
            @RequestBody @Valid final AuthMailRequest authMailRequest
    ) {
        AuthMailCommand authMailCommand = authMailRequest.toCommand();
        authCommandService.sendJoinAuthMail(authMailCommand);
        return ResponseHandler.handleResponse(
                HttpStatus.CREATED,
                ResponseMessage.AUTH_MAIL_SEND_COMPLETED
        );
    }

    @PostMapping(URL.PASSWORD_MAIL)
    public ResponseEntity<?> sendPasswordAuthMail(
            @RequestBody @Valid final AuthMailRequest authMailRequest
    ) {
        AuthMailCommand authMailCommand = authMailRequest.toCommand();
        authCommandService.sendPasswordAuthMail(authMailCommand);
        return ResponseHandler.handleResponse(
                HttpStatus.CREATED,
                ResponseMessage.AUTH_MAIL_SEND_COMPLETED
        );
    }
}
