package com.benchpress200.photique.auth.application;

import com.benchpress200.photique.auth.application.command.AuthMailCommand;
import com.benchpress200.photique.auth.application.exception.EmailAlreadyInUseException;
import com.benchpress200.photique.auth.domain.entity.EmailAuthCode;
import com.benchpress200.photique.auth.domain.port.AuthMailPort;
import com.benchpress200.photique.auth.domain.repository.EmailAuthCodeRepository;
import com.benchpress200.photique.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthCommandService {
    private final UserRepository userRepository;
    private final EmailAuthCodeRepository emailAuthCodeRepository;
    private final AuthMailPort authMailPort;

    public void sendJoinAuthMail(final AuthMailCommand authMailCommand) {
        String email = authMailCommand.getEmail();

        // 해당 이메일로 가입한 유저가 있는지 확인
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyInUseException(email);
        }

        // 메일전송
        // TODO: 이후 비동기 처리 고려
        String code = authMailPort.sendMail(email);

        // 인증코드 생성 및 저장
        EmailAuthCode emailAuthCode = EmailAuthCode.of(email, code);
        emailAuthCodeRepository.save(emailAuthCode);
    }
}
