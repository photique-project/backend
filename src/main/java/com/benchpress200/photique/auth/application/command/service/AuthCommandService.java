package com.benchpress200.photique.auth.application.command.service;

import com.benchpress200.photique.auth.application.command.model.AuthMailCodeValidateCommand;
import com.benchpress200.photique.auth.application.command.model.AuthMailCommand;
import com.benchpress200.photique.auth.application.command.model.AuthTokenRefreshCommand;
import com.benchpress200.photique.auth.application.command.result.AuthMailCodeValidateResult;
import com.benchpress200.photique.auth.application.command.result.AuthTokenResult;
import com.benchpress200.photique.auth.domain.entity.AuthMailCode;
import com.benchpress200.photique.auth.domain.enumeration.TokenValidationStatus;
import com.benchpress200.photique.auth.domain.exception.EmailAlreadyInUseException;
import com.benchpress200.photique.auth.domain.exception.EmailNotFoundException;
import com.benchpress200.photique.auth.domain.exception.InvalidRefreshTokenException;
import com.benchpress200.photique.auth.domain.exception.VerificationCodeNotFoundException;
import com.benchpress200.photique.auth.domain.port.mail.MailSenderPort;
import com.benchpress200.photique.auth.domain.port.persistence.AuthMailCodeCommandPort;
import com.benchpress200.photique.auth.domain.port.security.AuthenticationTokenManagerPort;
import com.benchpress200.photique.auth.domain.support.AuthCodeGenerator;
import com.benchpress200.photique.auth.domain.vo.AuthenticationTokens;
import com.benchpress200.photique.auth.domain.vo.MailContent;
import com.benchpress200.photique.auth.domain.vo.TokenValidationResult;
import com.benchpress200.photique.user.domain.port.persistence.UserQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthCommandService {
    private final AuthMailCodeCommandPort authMailCodeCommandPort;
    private final MailSenderPort mailSenderPort;
    private final UserQueryPort userQueryPort;
    private final AuthenticationTokenManagerPort authenticationTokenManagerPort;

    public void sendJoinAuthMail(AuthMailCommand authMailCommand) {
        String email = authMailCommand.getEmail();

        // 해당 이메일로 가입한 유저가 있는지 확인
        if (userQueryPort.existsByEmail(email)) {
            throw new EmailAlreadyInUseException(email);
        }

        // 메일전송
        sendMailTo(email);
    }

    public void sendPasswordAuthMail(AuthMailCommand authMailCommand) {
        String email = authMailCommand.getEmail();

        // 해당 이메일을 가진 유저가 존재하지 않는다면
        if (!userQueryPort.existsByEmail(email)) {
            throw new EmailNotFoundException(email);
        }

        // 메일전송
        sendMailTo(email);
    }

    public AuthMailCodeValidateResult validateAuthMailCode(
            AuthMailCodeValidateCommand authMailCodeValidateCommand) {
        String email = authMailCodeValidateCommand.getEmail();

        // 해당 이메일을 가진 코드 조회
        AuthMailCode authMailCode = authMailCodeCommandPort.findById(email)
                .orElseThrow(VerificationCodeNotFoundException::new);

        String code = authMailCode.getCode();
        boolean result = authMailCodeValidateCommand.validate(code);

        // 인증 코드가 유효하다면
        if (result) {
            authMailCode.verify();
            authMailCodeCommandPort.save(authMailCode);
        }

        return AuthMailCodeValidateResult.of(result);
    }

    public AuthTokenResult refreshAuthToken(AuthTokenRefreshCommand authTokenRefreshCommand) {
        // 토큰 유효성 검사 및 만료 기간 확인
        String refreshToken = authTokenRefreshCommand.getRefreshToken();
        TokenValidationResult tokenValidationResult = authenticationTokenManagerPort.validateToken(refreshToken);
        TokenValidationStatus status = tokenValidationResult.getStatus();

        // 유효하지 않으면 401
        if (status != TokenValidationStatus.VALID) {
            throw new InvalidRefreshTokenException();
        }

        // 유효하다면 인증 토큰 새로 발급
        long userId = tokenValidationResult.getUserId();
        String role = tokenValidationResult.getRole();
        AuthenticationTokens authenticationTokens = authenticationTokenManagerPort.issueTokens(userId, role);

        return AuthTokenResult.from(authenticationTokens);
    }

    private void sendMailTo(String email) {
        // TODO: 이후 비동기 처리 고려
        String code = AuthCodeGenerator.generate();
        MailContent mailContent = MailContent.of(code, email);
        mailSenderPort.sendMail(mailContent);

        // 인증코드 생성 및 저장
        AuthMailCode authMailCode = AuthMailCode.of(email, code);
        authMailCodeCommandPort.save(authMailCode);
    }
}
