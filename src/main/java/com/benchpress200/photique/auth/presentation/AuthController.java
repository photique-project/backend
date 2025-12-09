package com.benchpress200.photique.auth.presentation;

import com.benchpress200.photique.auth.application.AuthService;
import com.benchpress200.photique.auth.domain.dto.CodeValidationRequest;
import com.benchpress200.photique.common.constant.URL;
import com.benchpress200.photique.common.response.ApiSuccessResponse;
import com.benchpress200.photique.common.response.ResponseHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(URL.BASE_URL + URL.AUTH_DOMAIN)
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping(URL.VALIDATE_CODE)
    public ApiSuccessResponse<?> validateAuthMailCode(
            @RequestBody @Valid final CodeValidationRequest codeValidationRequest
    ) {
        authService.validateAuthMailCode(codeValidationRequest);
        return ResponseHandler.handleSuccessResponse(HttpStatus.NO_CONTENT);
    }
}
