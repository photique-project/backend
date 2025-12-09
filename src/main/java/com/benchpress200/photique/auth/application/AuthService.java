package com.benchpress200.photique.auth.application;


import com.benchpress200.photique.auth.domain.dto.CodeValidationRequest;

public interface AuthService {


    void validateAuthMailCode(CodeValidationRequest codeValidationRequest);
}
