package com.benchpress200.photique.auth.presentation;

import com.benchpress200.photique.auth.application.AuthService;
import com.benchpress200.photique.auth.domain.dto.AuthMailRequest;
import com.benchpress200.photique.auth.domain.dto.CodeValidationRequest;
import com.benchpress200.photique.auth.domain.dto.LoginRequest;
import com.benchpress200.photique.auth.domain.dto.WhoAmIResponse;
import com.benchpress200.photique.common.constant.URL;
import com.benchpress200.photique.common.response.ApiSuccessResponse;
import com.benchpress200.photique.common.response.ResponseHandler;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(URL.BASE_URL + URL.AUTH_DOMAIN)
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping(URL.LOGIN)
    public ApiSuccessResponse<?> login(
            @RequestBody final LoginRequest loginRequest,
            @RequestParam final boolean auto,
            final HttpServletResponse response // 쿠키 추가를 위한 응답 객체
    ) {
        // 응답 객체 조작은 컨트롤러 단에서 처리
        loginRequest.withAutoLogin(auto);
        Cookie accessTokenCookie = authService.login(loginRequest);
        response.addCookie(accessTokenCookie);

        return ResponseHandler.handleSuccessResponse(HttpStatus.CREATED);
    }

    @PostMapping(URL.LOGOUT)
    public ApiSuccessResponse<?> logout(
            @CookieValue(value = "Authorization", required = false) final String token,
            final HttpServletResponse response
    ) {
        Cookie expiredTokenCookie = authService.logout(token);
        response.addCookie(expiredTokenCookie);

        return ResponseHandler.handleSuccessResponse(HttpStatus.NO_CONTENT);
    }

    @PostMapping(URL.JOIN_MAIL)
    public ApiSuccessResponse<?> sendJoinAuthMail(
            @RequestBody @Valid final AuthMailRequest authMailRequest
    ) {
        authService.sendJoinAuthMail(authMailRequest);
        return ResponseHandler.handleSuccessResponse(HttpStatus.CREATED);
    }

    @PostMapping(URL.PASSWORD_MAIL)
    public ApiSuccessResponse<?> sendPasswordAuthMail(
            @RequestBody @Valid final AuthMailRequest authMailRequest
    ) {
        authService.sendPasswordAuthMail(authMailRequest);
        return ResponseHandler.handleSuccessResponse(HttpStatus.CREATED);
    }

    @PostMapping(URL.VALIDATE_CODE)
    public ApiSuccessResponse<?> validateAuthMailCode(
            @RequestBody @Valid final CodeValidationRequest codeValidationRequest
    ) {
        authService.validateAuthMailCode(codeValidationRequest);
        return ResponseHandler.handleSuccessResponse(HttpStatus.NO_CONTENT);
    }

    @GetMapping(URL.WHO_AM_I)
    public ApiSuccessResponse<?> whoAmI(
            @CookieValue("Authorization") final String accessToken
    ) {
        WhoAmIResponse whoAmIResponse = authService.whoAmI(accessToken);
        return ResponseHandler.handleSuccessResponse(whoAmIResponse, HttpStatus.OK);
    }
}
