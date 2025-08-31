package com.benchpress200.photique.user.application;

import com.benchpress200.photique.auth.domain.entity.AuthCode;
import com.benchpress200.photique.auth.domain.exception.MailAuthenticationCodeExpirationException;
import com.benchpress200.photique.auth.domain.exception.MailAuthenticationCodeNotVerifiedException;
import com.benchpress200.photique.auth.domain.repository.AuthCodeRepository;
import com.benchpress200.photique.image.domain.ImageUploaderPort;
import com.benchpress200.photique.user.application.command.JoinCommand;
import com.benchpress200.photique.user.application.command.ResetUserPasswordCommand;
import com.benchpress200.photique.user.application.command.UpdateUserDetailsCommand;
import com.benchpress200.photique.user.application.command.UpdateUserPasswordCommand;
import com.benchpress200.photique.user.application.exception.UserNotFoundException;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.entity.UserSearch;
import com.benchpress200.photique.user.domain.enumeration.Provider;
import com.benchpress200.photique.user.domain.enumeration.Role;
import com.benchpress200.photique.user.domain.event.DeleteProfileImageEvent;
import com.benchpress200.photique.user.domain.port.PasswordEncoderPort;
import com.benchpress200.photique.user.domain.repository.UserRepository;
import com.benchpress200.photique.user.domain.repository.UserSearchRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserCommandServiceImpl implements UserCommandService {
    @Value("${cloud.aws.s3.path.profile}")
    private String profileImagePath;

    private final ApplicationEventPublisher eventPublisher;
    private final AuthCodeRepository authCodeRepository;
    private final PasswordEncoderPort passwordEncoderPort;
    private final ImageUploaderPort imageUploaderPort;
    private final UserRepository userRepository;
    private final UserSearchRepository userSearchRepository;

    @Transactional
    public void join(final JoinCommand joinCommand) {
        // 이메일 인증 완료 여부 확인
        String email = joinCommand.getEmail();

        // 인증코드 있는지 확인
        AuthCode authCode = authCodeRepository.findById(email)
                .orElseThrow(MailAuthenticationCodeExpirationException::new);

        // 유저가 인증 완료한 코드인지 확인
        if (!authCode.isVerified()) {
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
            eventPublisher.publishEvent(new DeleteProfileImageEvent(uploadedImageUrl));
        }

        // 새 유저 엔티티 변환 및 저장
        User user = joinCommand.toEntity(
                encodedPassword,
                uploadedImageUrl,
                Provider.LOCAL,
                Role.USER
        );

        User newUser = userRepository.save(user);

        // 마지막으로 ES 저장해서 ES 저장 실패시 다 롤백 되도록
        UserSearch userSearch = UserSearch.builder()
                .id(newUser.getId())
                .profileImage(newUser.getProfileImage())
                .nickname(newUser.getNickname())
                .introduction(newUser.getIntroduction())
                .createdAt(newUser.getCreatedAt())
                .build();

        userSearchRepository.save(userSearch);
    }


    @Transactional
    public void updateUserDetails(final UpdateUserDetailsCommand updateUserDetailsCommand) {
        // 유저 조회
        Long userId = updateUserDetailsCommand.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // 닉네임 업데이트
        String newNickname = updateUserDetailsCommand.getNickname();
        user.updateNickname(newNickname);

        // 한 줄 소개 업데이트
        String newIntroduction = updateUserDetailsCommand.getIntroduction();
        user.updateIntroduction(newIntroduction);

        // 프로필 이미지 업데이트
        String oldPath = user.getProfileImage();
        MultipartFile newProfileImage = updateUserDetailsCommand.getProfileImage();
        String uploadedImageUrl = null;

        // 업데이트 이미지 설정헀다면 S3 업로드
        if (newProfileImage != null) {
            uploadedImageUrl = imageUploaderPort.update(newProfileImage, oldPath, profileImagePath);

            // 이미 이미지는 S3에 올라갔으니 롤백 감지하면 S3 이미지 삭제처리하도록 이벤트 발행
            eventPublisher.publishEvent(new DeleteProfileImageEvent(uploadedImageUrl));
        }

        user.updateProfileImage(uploadedImageUrl);
    }

    @Transactional
    public void updateUserPassword(final UpdateUserPasswordCommand updateUserPasswordCommand) {
        // 유저 조회
        Long userId = updateUserPasswordCommand.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // 비밀번호 업데이트
        String password = updateUserPasswordCommand.getPassword();
        String encodedPassword = passwordEncoderPort.encode(password);
        user.updatePassword(encodedPassword);
    }


    // 여기서 @Transactional이 없다면, 유저 엔티티를 조회한 후 엔티티 매니저를 close하기 떄문에
    // 변경 감지가 동작하지 않고 update 쿼리가 나가지 않음
    @Transactional
    public void resetUserPassword(final ResetUserPasswordCommand resetUserPasswordCommand) {
        // 유저 조회
        String email = resetUserPasswordCommand.getEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        // 비밀번호 업데이트
        String password = resetUserPasswordCommand.getPassword();
        String encodedPassword = passwordEncoderPort.encode(password);
        user.updatePassword(encodedPassword);
    }
}
