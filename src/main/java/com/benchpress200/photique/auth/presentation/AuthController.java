package com.benchpress200.photique.auth.presentation;

import com.benchpress200.photique.auth.application.AuthService;
import com.benchpress200.photique.auth.domain.dto.LoginRequest;
import com.benchpress200.photique.common.response.ApiSuccessResponse;
import com.benchpress200.photique.common.response.ResponseHandler;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ApiSuccessResponse<?> login(
            final @RequestBody LoginRequest loginRequest,
            final HttpServletResponse response
    ) {
        Cookie accessTokenCookie = authService.login(loginRequest);
        response.addCookie(accessTokenCookie);

        return ResponseHandler.handleSuccessResponse(HttpStatus.CREATED);
    }
}
