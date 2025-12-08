package com.benchpress200.photique.auth.application;

import com.benchpress200.photique.auth.domain.dto.CodeValidationRequest;
import com.benchpress200.photique.auth.presentation.request.AuthMailRequest;
import com.benchpress200.photique.user.domain.UserDomainService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserDomainService userDomainService;

    @Override
    public void sendPasswordAuthMail(final AuthMailRequest authMailRequest) {
        // 해당 이메일 가입자 확인
        String email = authMailRequest.getEmail();
        userDomainService.findUser(email);

        // 메일전송
//        authDomainService.sendMail(email);
    }

    @Override
    public void validateAuthMailCode(final CodeValidationRequest codeValidationRequest) {
        // 인증코드 검증
        String email = codeValidationRequest.getEmail();
        String code = codeValidationRequest.getCode();
//        authDomainService.validateAuthMailCode(email, code);
    }
}
