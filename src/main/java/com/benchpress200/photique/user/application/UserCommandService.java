package com.benchpress200.photique.user.application;

import com.benchpress200.photique.auth.domain.entity.EmailAuthCode;
import com.benchpress200.photique.auth.domain.exception.MailAuthenticationCodeExpirationException;
import com.benchpress200.photique.auth.domain.exception.MailAuthenticationCodeNotVerifiedException;
import com.benchpress200.photique.auth.domain.port.AuthenticationUserProviderPort;
import com.benchpress200.photique.auth.domain.repository.EmailAuthCodeRepository;
import com.benchpress200.photique.image.domain.event.ImageEventPublisher;
import com.benchpress200.photique.image.domain.port.ImageUploaderPort;
import com.benchpress200.photique.user.application.command.JoinCommand;
import com.benchpress200.photique.user.application.command.ResetUserPasswordCommand;
import com.benchpress200.photique.user.application.command.UpdateUserDetailsCommand;
import com.benchpress200.photique.user.application.command.UpdateUserPasswordCommand;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.enumeration.Provider;
import com.benchpress200.photique.user.domain.enumeration.Role;
import com.benchpress200.photique.user.domain.exception.DuplicatedUserException;
import com.benchpress200.photique.user.domain.exception.UserNotFoundException;
import com.benchpress200.photique.user.domain.port.PasswordEncoderPort;
import com.benchpress200.photique.user.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class UserCommandService {
    @Value("${cloud.aws.s3.path.profile}")
    private String profileImagePath;

    private final AuthenticationUserProviderPort authenticationUserProviderPort;
    private final ImageEventPublisher imageEventPublisher;
    private final EmailAuthCodeRepository emailAuthCodeRepository;
    private final PasswordEncoderPort passwordEncoderPort;
    private final ImageUploaderPort imageUploaderPort;
    private final UserRepository userRepository;

    public void join(JoinCommand joinCommand) {
        // 이메일 인증 완료 여부 확인
        String email = joinCommand.getEmail();

        // 인증 코드 있는지 확인
        EmailAuthCode emailAuthCode = emailAuthCodeRepository.findById(email)
                .orElseThrow(MailAuthenticationCodeExpirationException::new);

        // 유저가 인증 완료한 코드인지 확인
        if (!emailAuthCode.isVerified()) {
            throw new MailAuthenticationCodeNotVerifiedException();
        }

        // 비밀번호 인코딩
        String rawPassword = joinCommand.getPassword();
        String encodedPassword = passwordEncoderPort.encode(rawPassword);

        // 이미지 S3 업로드
        MultipartFile profileImage = joinCommand.getProfileImage();
        String uploadedImageUrl = null;

        // 이미지 설정헀다면 S3 업로드
        if (profileImage != null) {
            uploadedImageUrl = imageUploaderPort.upload(profileImage, profileImagePath);

            // 이미 이미지는 S3에 올라갔으니 롤백감지하면 S3 이미지 삭제처리하도록 이벤트 발행
            imageEventPublisher.publishImageDeleteEventIfRollback(uploadedImageUrl);
        }

        // 새 유저 엔티티 변환 및 저장
        User user = joinCommand.toEntity(
                encodedPassword,
                uploadedImageUrl,
                Provider.LOCAL,
                Role.USER
        );

        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            // 중복된 이메일 or 중복된 닉네임
            throw new DuplicatedUserException();
        }
    }

    public void updateUserDetails(UpdateUserDetailsCommand updateUserDetailsCommand) {
        // 유저 조회
        Long userId = updateUserDetailsCommand.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // 닉네임 업데이트
        String newNickname = updateUserDetailsCommand.getNickname();

        if (newNickname != null) { // null이면 닉네임 유지
            user.updateNickname(newNickname);
        }

        // 한 줄 소개 업데이트
        String newIntroduction = updateUserDetailsCommand.getIntroduction();

        if (newIntroduction != null) { // null이면 소개 유지
            user.updateIntroduction(newIntroduction);
        }

        // 프로필 이미지 업데이트
        MultipartFile newProfileImage = updateUserDetailsCommand.getProfileImage();

        if (newProfileImage != null) { // null이면 프로필 이미지 유지
            String oldProfileImageUrl = user.getProfileImage();
            imageEventPublisher.publishImageDeleteEventIfCommit(oldProfileImageUrl);

            if (newProfileImage.isEmpty()) { // 기본값으로 업데이트
                user.updateProfileImage(null);
            } else {
                String newProfileImageUrl = imageUploaderPort.upload(newProfileImage, profileImagePath);
                // 이미 이미지는 S3에 올라갔으니 롤백감지하면 S3 이미지 삭제처리하도록 이벤트 발행
                imageEventPublisher.publishImageDeleteEventIfRollback(newProfileImageUrl);
                user.updateProfileImage(newProfileImageUrl);
            }
        }

        // TODO: 해당 유저가 게시한 단일 작품, 전시회 검색 데이터(ES) 비동기 업데이트 메시징 필요
    }

    // 여기서 @Transactional이 없다면, 유저 엔티티를 조회한 후 엔티티 매니저를 close하기 떄문에
    // 변경 감지가 동작하지 않고 update 쿼리가 나가지 않음
    public void updateUserPassword(UpdateUserPasswordCommand updateUserPasswordCommand) {
        // 유저 조회
        Long userId = updateUserPasswordCommand.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // 비밀번호 업데이트
        String password = updateUserPasswordCommand.getPassword();
        String encodedPassword = passwordEncoderPort.encode(password);
        user.updatePassword(encodedPassword);
    }

    public void resetUserPassword(ResetUserPasswordCommand resetUserPasswordCommand) {
        // 유저 조회
        String email = resetUserPasswordCommand.getEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        // 인증 코드 있는지 확인
        EmailAuthCode emailAuthCode = emailAuthCodeRepository.findById(email)
                .orElseThrow(MailAuthenticationCodeExpirationException::new);

        // 유저가 인증 완료한 코드인지 확인
        if (!emailAuthCode.isVerified()) {
            throw new MailAuthenticationCodeNotVerifiedException();
        }

        // 비밀번호 업데이트
        String password = resetUserPasswordCommand.getPassword();
        String encodedPassword = passwordEncoderPort.encode(password);
        user.updatePassword(encodedPassword);
    }

    public void withdraw(Long userId) {
        // 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // 소프트 딜리트
        // 해당 유저 로그인 & 상세조회 불가능
        // 해당 유저가 작성한 게시글 조회 가능
        user.markAsDeleted();
    }
}
