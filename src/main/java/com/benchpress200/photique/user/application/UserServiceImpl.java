package com.benchpress200.photique.user.application;

import com.benchpress200.photique.auth.infrastructure.TokenManager;
import com.benchpress200.photique.common.infrastructure.ImageUploader;
import com.benchpress200.photique.user.domain.dto.JoinRequest;
import com.benchpress200.photique.user.domain.dto.UserDetailResponse;
import com.benchpress200.photique.user.domain.dto.UserIdResponse;
import com.benchpress200.photique.user.domain.dto.UserUpdateRequest;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.exception.UserException;
import com.benchpress200.photique.user.infrastructure.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    @Value("${cloud.aws.s3.path.profile}")
    private String profileImagePath;

    private final ImageUploader imageUploader;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenManager tokenManager;

    @Override
    public void join(final JoinRequest joinRequest) {
        String password = passwordEncoder.encode(joinRequest.getPassword());
        String imageUrl = uploadProfileImage(joinRequest.getProfileImage());
        User user = joinRequest.toEntity(password, imageUrl);

        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new UserException("Email or nickname already exists", e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @Override
    public UserDetailResponse getUserDetail(final Long userId) {
        User foundUser = findUserById(userId);
        return UserDetailResponse.from(foundUser);
    }

    @Override
    public void updateUserDetail(
            final Long userId,
            final UserUpdateRequest userUpdateRequest
    ) {
        User user = findUserById(userId);

        // 비밀번호 업데이트
        if (userUpdateRequest.hasPassword()) {
            updatePassword(user, userUpdateRequest.getPassword());
        }

        // 닉네임 업데이트
        if (userUpdateRequest.hasNickname()) {
            updateNickname(user, userUpdateRequest.getNickname());
        }

        // 한 줄 소개 업데이트
        // 빈 값으로 업데이트 한다면 isEmpty() 호출할 수 있도록 빈 문자열 전달됨
        if (userUpdateRequest.hasIntroduction()) {
            updateIntroduction(user, userUpdateRequest.getIntroduction());
        }

        // 프로필 이미지 업데이트
        // null이면 업데이트 X
        // 기본값 설정이면 빈 객체전달
        if (userUpdateRequest.hasProfileImage()) {
            String profileImage = user.getProfileImage();

            if (userUpdateRequest.isDefaultProfileImage()) { // 기본값 설정을 원한다면
                imageUploader.delete(profileImage);
                user.updateDefaultProfileImage();
                return;

            }

            // 기본값 설정이 아닌 업데이트라면
            profileImage = imageUploader.update(
                    userUpdateRequest.getProfileImage(),
                    profileImage,
                    profileImagePath
            );

            user.updateProfileImage(profileImage);
        }
    }

    @Override
    public UserIdResponse getUserId(final String accessToken) {
        Long userId = tokenManager.getUserId(accessToken);
        return UserIdResponse.builder()
                .id(userId)
                .build();
    }

    // TODO: 이후에 해당 유저 관련 데이터 모두 삭제하는 코드 추가해야함
    @Override
    public void withdraw(final Long userId) {
        // 유저있는지확인
        userRepository.findById(userId).orElseThrow(
                () -> new UserException("User with ID {" + userId + "} is not found", HttpStatus.NOT_FOUND));

        userRepository.deleteById(userId);
    }

    private String uploadProfileImage(final MultipartFile profileImage) {
        if (profileImage != null) {
            return imageUploader.upload(profileImage, profileImagePath);
        }

        return null;
    }

    private User findUserById(final Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new UserException("User with ID {" + userId + "} is not found", HttpStatus.NOT_FOUND));
    }

    private void updatePassword(
            final User user,
            final String password
    ) {
        user.updatePassword(passwordEncoder.encode(password));
    }

    private void updateNickname(
            final User user,
            final String nickname
    ) {
        userRepository.findByNickname(nickname)
                .ifPresent(foundUser -> {
                    throw new UserException("{" + nickname + "} is already in use",
                            HttpStatus.CONFLICT);
                });

        user.updateNickname(nickname);
    }

    private void updateIntroduction(
            final User user,
            final String introduction
    ) {
        user.updateIntroduction(introduction);
    }
}
