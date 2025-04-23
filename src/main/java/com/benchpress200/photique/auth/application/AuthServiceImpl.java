package com.benchpress200.photique.auth.application;

import com.benchpress200.photique.auth.domain.AuthDomainService;
import com.benchpress200.photique.auth.domain.dto.AuthMailRequest;
import com.benchpress200.photique.auth.domain.dto.CodeValidationRequest;
import com.benchpress200.photique.auth.domain.dto.LoginRequest;
import com.benchpress200.photique.auth.domain.dto.WhoAmIResponse;
import com.benchpress200.photique.user.domain.UserDomainService;
import com.benchpress200.photique.user.domain.entity.User;
import jakarta.servlet.http.Cookie;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthDomainService authDomainService;
    private final UserDomainService userDomainService;

    @Override
    public Cookie login(final LoginRequest loginRequest) {
        // 이메일로 유저 조회
        String email = loginRequest.getEmail();
        User user = userDomainService.findUser(email);

        // 비밀번호 확인
        String loginPassword = loginRequest.getPassword();
        String password = user.getPassword();
        authDomainService.validatePassword(loginPassword, password);

        // 자동 로그인 확인
        boolean auto = loginRequest.isAuto();

        // 토큰발급
        return authDomainService.issueToken(user, auto);
    }

    @Override
    public Cookie logout(final String token) {
        // 토큰 폐기
        return authDomainService.expireToken(token);
    }

    @Override
    public void sendJoinAuthMail(final AuthMailRequest authMailRequest) {
        // 해당 이메일 가입자가 있는지 확인
        String email = authMailRequest.getEmail();
        userDomainService.isDuplicatedEmail(email);

        // 메일전송
        authDomainService.sendMail(email);
    }

    @Override
    public void sendPasswordAuthMail(final AuthMailRequest authMailRequest) {
        // 해당 이메일 가입자 확인
        String email = authMailRequest.getEmail();
        userDomainService.findUser(email);

        // 메일전송
        authDomainService.sendMail(email);
    }

    @Override
    public void validateAuthMailCode(final CodeValidationRequest codeValidationRequest) {
        // 인증코드 검증
        String email = codeValidationRequest.getEmail();
        String code = codeValidationRequest.getCode();
        authDomainService.validateAuthMailCode(email, code);
    }

    @Override
    public WhoAmIResponse whoAmI(final String accessToken) {
        Long userId = authDomainService.extractUserId(accessToken);

        return WhoAmIResponse.builder()
                .id(userId)
                .build();
    }
}
