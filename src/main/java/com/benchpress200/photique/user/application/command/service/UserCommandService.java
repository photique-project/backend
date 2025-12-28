package com.benchpress200.photique.user.application.command.service;

import com.benchpress200.photique.auth.domain.entity.AuthMailCode;
import com.benchpress200.photique.auth.domain.exception.MailAuthenticationCodeExpirationException;
import com.benchpress200.photique.auth.domain.exception.MailAuthenticationCodeNotVerifiedException;
import com.benchpress200.photique.auth.domain.port.persistence.AuthMailCodeQueryPort;
import com.benchpress200.photique.image.domain.event.DeleteImageEvent;
import com.benchpress200.photique.image.domain.event.UploadImageEvent;
import com.benchpress200.photique.image.domain.port.event.ImageEventPublishPort;
import com.benchpress200.photique.image.domain.port.storage.ImageUploaderPort;
import com.benchpress200.photique.user.application.command.model.JoinCommand;
import com.benchpress200.photique.user.application.command.model.UserDetailsUpdateCommand;
import com.benchpress200.photique.user.application.command.model.UserPasswordResetCommand;
import com.benchpress200.photique.user.application.command.model.UserPasswordUpdateCommand;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.enumeration.Provider;
import com.benchpress200.photique.user.domain.enumeration.Role;
import com.benchpress200.photique.user.domain.event.UpdateUserDetailsEvent;
import com.benchpress200.photique.user.domain.exception.DuplicatedUserException;
import com.benchpress200.photique.user.domain.exception.UserNotFoundException;
import com.benchpress200.photique.user.domain.port.event.UserEventPublishPort;
import com.benchpress200.photique.user.domain.port.persistence.UserCommandPort;
import com.benchpress200.photique.user.domain.port.persistence.UserQueryPort;
import com.benchpress200.photique.user.domain.port.security.PasswordEncoderPort;
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

    private final UserCommandPort userCommandPort;
    private final UserQueryPort userQueryPort;
    private final UserEventPublishPort userEventPublishPort;

    private final ImageUploaderPort imageUploaderPort;
    private final ImageEventPublishPort imageEventPublishPort;

    private final PasswordEncoderPort passwordEncoderPort;
    private final AuthMailCodeQueryPort authMailCodeQueryPort;

    public void join(JoinCommand joinCommand) {
        // 이메일 인증 완료 여부 확인
        String email = joinCommand.getEmail();

        // 인증 코드 있는지 확인
        AuthMailCode authMailCode = authMailCodeQueryPort.findById(email)
                .orElseThrow(MailAuthenticationCodeExpirationException::new);

        // 유저가 인증 완료한 코드인지 확인
        if (!authMailCode.isVerified()) {
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
            UploadImageEvent uploadImageEvent = UploadImageEvent.of(uploadedImageUrl);
            imageEventPublishPort.publishUploadImageEvent(uploadImageEvent);
        }

        // 새 유저 엔티티 변환 및 저장
        User user = joinCommand.toEntity(
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

    public void updateUserDetails(UserDetailsUpdateCommand userDetailsUpdateCommand) {
        // 유저 조회
        Long userId = userDetailsUpdateCommand.getUserId();
        User user = userQueryPort.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // 닉네임 업데이트
        String newNickname = userDetailsUpdateCommand.getNickname();

        if (newNickname != null) { // null이면 닉네임 유지
            user.updateNickname(newNickname);
        }

        // 한 줄 소개 업데이트
        String newIntroduction = userDetailsUpdateCommand.getIntroduction();

        if (newIntroduction != null) { // null이면 소개 유지
            user.updateIntroduction(newIntroduction);
        }

        // 프로필 이미지 업데이트
        MultipartFile newProfileImage = userDetailsUpdateCommand.getProfileImage();

        if (newProfileImage != null) { // null이면 프로필 이미지 유지
            String oldProfileImageUrl = user.getProfileImage();
            DeleteImageEvent deleteImageEvent = DeleteImageEvent.of(oldProfileImageUrl);
            imageEventPublishPort.publishDeleteImageEvent(deleteImageEvent);

            if (newProfileImage.isEmpty()) { // 기본값으로 업데이트
                user.updateProfileImage(null);
            } else {
                String newProfileImageUrl = imageUploaderPort.upload(newProfileImage, profileImagePath);
                // 이미 이미지는 S3에 올라갔으니 롤백감지하면 S3 이미지 삭제처리하도록 이벤트 발행
                UploadImageEvent uploadImageEvent = UploadImageEvent.of(newProfileImageUrl);
                imageEventPublishPort.publishUploadImageEvent(uploadImageEvent);
                user.updateProfileImage(newProfileImageUrl);
            }
        }

        // 유저 업데이트 이벤트 발행 => ES에 저장된 작품의 작가 데이터 동기화
        UpdateUserDetailsEvent updateUserDetailsEvent = UpdateUserDetailsEvent.of(userId);
        userEventPublishPort.publishUpdateUserDetailsEvent(updateUserDetailsEvent);
    }

    // 여기서 @Transactional이 없다면, 유저 엔티티를 조회한 후 엔티티 매니저를 close하기 떄문에
    // 변경 감지가 동작하지 않고 update 쿼리가 나가지 않음
    public void updateUserPassword(UserPasswordUpdateCommand userPasswordUpdateCommand) {
        // 유저 조회
        Long userId = userPasswordUpdateCommand.getUserId();
        User user = userQueryPort.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // 비밀번호 업데이트
        String password = userPasswordUpdateCommand.getPassword();
        String encodedPassword = passwordEncoderPort.encode(password);
        user.updatePassword(encodedPassword);
    }

    public void resetUserPassword(UserPasswordResetCommand userPasswordResetCommand) {
        // 유저 조회
        String email = userPasswordResetCommand.getEmail();
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
        String password = userPasswordResetCommand.getPassword();
        String encodedPassword = passwordEncoderPort.encode(password);
        user.updatePassword(encodedPassword);
    }

    public void withdraw(Long userId) {
        // 유저 조회
        User user = userQueryPort.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // 소프트 딜리트
        // 해당 유저 로그인 & 상세조회 불가능
        // 해당 유저가 작성한 게시글 조회 가능
        user.markAsDeleted();
    }
}
