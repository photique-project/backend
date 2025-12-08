package com.benchpress200.photique.auth.application;


import com.benchpress200.photique.auth.domain.dto.CodeValidationRequest;
import com.benchpress200.photique.auth.presentation.request.AuthMailRequest;

public interface AuthService {

    void sendPasswordAuthMail(AuthMailRequest authMailRequest);

    void validateAuthMailCode(CodeValidationRequest codeValidationRequest);
}
