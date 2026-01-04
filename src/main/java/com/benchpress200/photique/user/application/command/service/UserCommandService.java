package com.benchpress200.photique.user.application.command.service;

import com.benchpress200.photique.auth.application.query.port.out.persistence.AuthMailCodeQueryPort;
import com.benchpress200.photique.auth.domain.entity.AuthMailCode;
import com.benchpress200.photique.auth.domain.exception.MailAuthenticationCodeExpirationException;
import com.benchpress200.photique.auth.domain.exception.MailAuthenticationCodeNotVerifiedException;
import com.benchpress200.photique.image.domain.port.storage.ImageUploaderPort;
import com.benchpress200.photique.user.application.command.model.ResisterCommand;
import com.benchpress200.photique.user.application.command.model.UserDetailsUpdateCommand;
import com.benchpress200.photique.user.application.command.model.UserPasswordResetCommand;
import com.benchpress200.photique.user.application.command.model.UserPasswordUpdateCommand;
import com.benchpress200.photique.user.application.command.port.in.ResetUserPasswordUseCase;
import com.benchpress200.photique.user.application.command.port.in.ResisterUseCase;
import com.benchpress200.photique.user.application.command.port.in.UpdateUserDetailsUseCase;
import com.benchpress200.photique.user.application.command.port.in.UpdateUserPasswordUseCase;
import com.benchpress200.photique.user.application.command.port.in.WithdrawUseCase;
import com.benchpress200.photique.user.application.command.port.out.event.UserEventPublishPort;
import com.benchpress200.photique.user.application.command.port.out.persistence.UserCommandPort;
import com.benchpress200.photique.user.application.command.port.out.security.PasswordEncoderPort;
import com.benchpress200.photique.user.application.query.port.out.persistence.UserQueryPort;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.enumeration.Provider;
import com.benchpress200.photique.user.domain.enumeration.Role;
import com.benchpress200.photique.user.domain.event.UserDetailsUpdateEvent;
import com.benchpress200.photique.user.domain.event.UserProfileImageDeleteEvent;
import com.benchpress200.photique.user.domain.event.UserProfileImageUploadEvent;
import com.benchpress200.photique.user.domain.exception.DuplicatedUserException;
import com.benchpress200.photique.user.domain.exception.UserNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class UserCommandService implements
        ResisterUseCase,
        UpdateUserDetailsUseCase,
        UpdateUserPasswordUseCase,
        ResetUserPasswordUseCase,
        WithdrawUseCase {
    @Value("${cloud.aws.s3.path.profile}")
    private String profileImagePath;

    private final UserCommandPort userCommandPort;
    private final UserQueryPort userQueryPort;
    private final UserEventPublishPort userEventPublishPort;

    private final ImageUploaderPort imageUploaderPort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final AuthMailCodeQueryPort authMailCodeQueryPort;

    public void resister(ResisterCommand command) {
        // 이메일 인증 완료 여부 확인
        String email = command.getEmail();

        // 인증 코드 있는지 확인
        AuthMailCode authMailCode = authMailCodeQueryPort.findById(email)
                .orElseThrow(MailAuthenticationCodeExpirationException::new);

        // 유저가 인증 완료한 코드인지 확인
        if (!authMailCode.isVerified()) {
            throw new MailAuthenticationCodeNotVerifiedException();
        }

        // 비밀번호 인코딩
        String rawPassword = command.getPassword();
        String encodedPassword = passwordEncoderPort.encode(rawPassword);

        // 이미지 S3 업로드
        MultipartFile profileImage = command.getProfileImage();
        String uploadedImageUrl = null;

        // 이미지 설정헀다면 S3 업로드
        if (profileImage != null) {
            uploadedImageUrl = imageUploaderPort.upload(profileImage, profileImagePath);

            // 회원가입 유스케이스 롤백 시 업로드한 이미지 삭제하는 이벤트 발행
            UserProfileImageUploadEvent event = UserProfileImageUploadEvent.of(uploadedImageUrl);
            userEventPublishPort.publishUserProfileImageUploadEvent(event);
        }

        // 새 유저 엔티티 변환 및 저장
        User user = command.toEntity(
                encodedPassword,
                uploadedImageUrl,
                Provider.LOCAL,
                Role.USER
        );

        try {
            userCommandPort.save(user);
        } catch (DataIntegrityViolationException e) {
            // 중복된 이메일 or 중복된 닉네임
            throw new DuplicatedUserException();
        }
    }

    public void updateUserDetails(UserDetailsUpdateCommand command) {
        // 유저 조회
        Long userId = command.getUserId();
        User user = userQueryPort.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // 닉네임 업데이트
        String newNickname = command.getNickname();

        if (newNickname != null) { // null이면 닉네임 유지
            user.updateNickname(newNickname);
        }

        // 한 줄 소개 업데이트
        String newIntroduction = command.getIntroduction();

        if (newIntroduction != null) { // null이면 소개 유지
            user.updateIntroduction(newIntroduction);
        }

        // 프로필 이미지 업데이트
        MultipartFile newProfileImage = command.getProfileImage();

        if (newProfileImage != null) { // null이면 프로필 이미지 유지
            String oldProfileImageUrl = user.getProfileImage();
            UserProfileImageDeleteEvent userProfileImageDeleteEvent = UserProfileImageDeleteEvent.of(
                    oldProfileImageUrl);
            userEventPublishPort.publishUserProfileImageDeleteEvent(
                    userProfileImageDeleteEvent); // 트랜잭션 커밋 시 이미지 삭제 처리 이벤트 발행

            if (newProfileImage.isEmpty()) { // 기본값으로 업데이트
                user.updateProfileImage(null);
            } else {
                String newProfileImageUrl = imageUploaderPort.upload(newProfileImage, profileImagePath);
                user.updateProfileImage(newProfileImageUrl);

                UserProfileImageUploadEvent userProfileImageUploadEvent = UserProfileImageUploadEvent.of(
                        newProfileImageUrl);
                userEventPublishPort.publishUserProfileImageUploadEvent(
                        userProfileImageUploadEvent); // 트랜잭션 롤백 시 이미지 삭제 처리 이벤트 발행
            }
        }

        UserDetailsUpdateEvent userDetailsUpdateEvent = UserDetailsUpdateEvent.of(userId);
        userEventPublishPort.publishUserDetailsUpdateEvent(userDetailsUpdateEvent); // 트랜잭션 커밋 시 작가 데이터 ES 동기화 이벤트 발행
    }

    // 여기서 @Transactional이 없다면, 유저 엔티티를 조회한 후 엔티티 매니저를 close하기 떄문에
    // 변경 감지가 동작하지 않고 update 쿼리가 나가지 않음
    public void updateUserPassword(UserPasswordUpdateCommand command) {
        // 유저 조회
        Long userId = command.getUserId();
        User user = userQueryPort.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // 비밀번호 업데이트
        String password = command.getPassword();
        String encodedPassword = passwordEncoderPort.encode(password);
        user.updatePassword(encodedPassword);
    }

    public void resetUserPassword(UserPasswordResetCommand command) {
        // 유저 조회
        String email = command.getEmail();
        User user = userQueryPort.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        // 인증 코드 있는지 확인
        AuthMailCode authMailCode = authMailCodeQueryPort.findById(email)
                .orElseThrow(MailAuthenticationCodeExpirationException::new);

        // 유저가 인증 완료한 코드인지 확인
        if (!authMailCode.isVerified()) {
            throw new MailAuthenticationCodeNotVerifiedException();
        }

        // 비밀번호 업데이트
        String password = command.getPassword();
        String encodedPassword = passwordEncoderPort.encode(password);
        user.updatePassword(encodedPassword);
    }

    public void withdraw(Long userId) {
        // 소프트 딜리트
        // 해당 유저 로그인 불가능
        // 해당 유저가 작성한 게시글 조회 가능
        userQueryPort.findById(userId)
                .ifPresent(User::remove);
    }
}
