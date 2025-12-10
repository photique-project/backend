package com.benchpress200.photique.auth.application;

import com.benchpress200.photique.auth.application.command.AuthMailCodeValidationCommand;
import com.benchpress200.photique.auth.application.command.AuthMailCommand;
import com.benchpress200.photique.auth.application.command.AuthTokenRefreshCommand;
import com.benchpress200.photique.auth.application.exception.EmailAlreadyInUseException;
import com.benchpress200.photique.auth.application.exception.EmailNotFoundException;
import com.benchpress200.photique.auth.application.exception.InvalidRefreshTokenException;
import com.benchpress200.photique.auth.application.exception.VerificationCodeNotFoundException;
import com.benchpress200.photique.auth.application.result.AuthMailCodeValidationResult;
import com.benchpress200.photique.auth.application.result.AuthTokenResult;
import com.benchpress200.photique.auth.domain.entity.EmailAuthCode;
import com.benchpress200.photique.auth.domain.enumeration.TokenValidationStatus;
import com.benchpress200.photique.auth.domain.port.AuthMailPort;
import com.benchpress200.photique.auth.domain.port.AuthenticationTokenManagerPort;
import com.benchpress200.photique.auth.domain.repository.EmailAuthCodeRepository;
import com.benchpress200.photique.auth.domain.result.AuthenticationTokens;
import com.benchpress200.photique.auth.domain.result.TokenValidationResult;
import com.benchpress200.photique.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthCommandService {
    private final UserRepository userRepository;
    private final EmailAuthCodeRepository emailAuthCodeRepository;
    private final AuthenticationTokenManagerPort authenticationTokenManagerPort;
    private final AuthMailPort authMailPort;

    public void sendJoinAuthMail(final AuthMailCommand authMailCommand) {
        String email = authMailCommand.getEmail();

        // 해당 이메일로 가입한 유저가 있는지 확인
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyInUseException(email);
        }

        // 메일전송
        sendMailTo(email);
    }

    public void sendPasswordAuthMail(final AuthMailCommand authMailCommand) {
        String email = authMailCommand.getEmail();

        // 해당 이메일을 가진 유저가 존재하지 않는다면
        if (!userRepository.existsByEmail(email)) {
            throw new EmailNotFoundException(email);
        }

        // 메일전송
        sendMailTo(email);
    }

    public AuthMailCodeValidationResult validateAuthMailCode(
            final AuthMailCodeValidationCommand authMailCodeValidationCommand
    ) {
        String email = authMailCodeValidationCommand.getEmail();

        // 해당 이메일을 가진 코드 조회
        EmailAuthCode emailAuthCode = emailAuthCodeRepository.findById(email)
                .orElseThrow(VerificationCodeNotFoundException::new);

        String code = emailAuthCode.getCode();
        boolean result = authMailCodeValidationCommand.validate(code);

        // 인증 코드가 유효하다면
        if (result) {
            emailAuthCode.verify();
            emailAuthCodeRepository.save(emailAuthCode);
        }

        return AuthMailCodeValidationResult.of(result);
    }

    public AuthTokenResult refreshAuthToken(final AuthTokenRefreshCommand authTokenRefreshCommand) {
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

    private void sendMailTo(final String email) {
        // TODO: 이후 비동기 처리 고려
        String code = authMailPort.sendMail(email);

        // 인증코드 생성 및 저장
        EmailAuthCode emailAuthCode = EmailAuthCode.of(email, code);
        emailAuthCodeRepository.save(emailAuthCode);
    }
}
