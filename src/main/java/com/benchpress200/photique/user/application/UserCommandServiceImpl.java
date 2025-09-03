package com.benchpress200.photique.user.application;

import com.benchpress200.photique.auth.domain.entity.AuthCode;
import com.benchpress200.photique.auth.domain.exception.MailAuthenticationCodeExpirationException;
import com.benchpress200.photique.auth.domain.exception.MailAuthenticationCodeNotVerifiedException;
import com.benchpress200.photique.auth.domain.repository.AuthCodeRepository;
import com.benchpress200.photique.image.domain.ImageUploaderPort;
import com.benchpress200.photique.image.domain.event.ImageUploadRollbackEvent;
import com.benchpress200.photique.user.application.command.JoinCommand;
import com.benchpress200.photique.user.application.command.ResetUserPasswordCommand;
import com.benchpress200.photique.user.application.command.UpdateUserDetailsCommand;
import com.benchpress200.photique.user.application.command.UpdateUserPasswordCommand;
import com.benchpress200.photique.user.application.exception.UserNotFoundException;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.entity.UserSearch;
import com.benchpress200.photique.user.domain.enumeration.Provider;
import com.benchpress200.photique.user.domain.enumeration.Role;
import com.benchpress200.photique.user.domain.event.UserSearchSaveRollbackEvent;
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

        // 인증 코드 있는지 확인
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
            eventPublisher.publishEvent(new ImageUploadRollbackEvent(uploadedImageUrl));
        }

        // 새 유저 엔티티 변환 및 저장
        User user = joinCommand.toEntity(
                encodedPassword,
                uploadedImageUrl,
                Provider.LOCAL,
                Role.USER
        );

        // 유저 엔티티의 id 생성 전략이 DB의 Auto Increment이기 때문에 즉시 쿼리 실행됨
        // 이후 트랜잭션이 끝나는 시점에 commit만 나감
        User newUser = userRepository.save(user);

        // ES에 저장할 유저 검색 엔티티 생성
        UserSearch userSearch = UserSearch.builder()
                .id(newUser.getId())
                .profileImage(newUser.getProfileImage())
                .nickname(newUser.getNickname())
                .introduction(newUser.getIntroduction())
                .createdAt(newUser.getCreatedAt())
                .build();

        userSearchRepository.save(userSearch);

        // ES 저장 성공하면 이후에 커밋 이벤트 장애 발생하면 롤백하도록 이벤트 발행
        eventPublisher.publishEvent(new UserSearchSaveRollbackEvent(newUser.getId()));
        // TODO: 테스트 코드 확인할 차례
    }


    @Transactional
    public void updateUserDetails(final UpdateUserDetailsCommand updateUserDetailsCommand) {
        // 유저 조회
        Long userId = updateUserDetailsCommand.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        UserSearch userSearch = userSearchRepository.findById(userId)
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
            eventPublisher.publishEvent(new ImageUploadRollbackEvent(uploadedImageUrl));
        }

        user.updateProfileImage(uploadedImageUrl);
        // TODO: 검색 데이터에도 저장해야함, NULL을 줘서 변경안해는 것도 가능하도록
        // es롤백도 보장하도록
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

    @Transactional
    public void withdraw(final Long userId) {
        // 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // 소프트 딜리트
        // 해당 유저 로그인 & 상세조회 불가능
        // 해당 유저가 작성한 게시글 조회 가능
        user.markAsDeleted();

        // 유저 검색 데이터 삭제
        userSearchRepository.deleteById(userId);
    }
}
