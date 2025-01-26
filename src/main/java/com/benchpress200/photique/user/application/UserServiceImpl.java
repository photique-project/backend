package com.benchpress200.photique.user.application;

import com.benchpress200.photique.auth.infrastructure.TokenManager;
import com.benchpress200.photique.common.infrastructure.ImageUploader;
import com.benchpress200.photique.user.domain.dto.JoinRequest;
import com.benchpress200.photique.user.domain.dto.UpdateUserRequest;
import com.benchpress200.photique.user.domain.dto.UserIdResponse;
import com.benchpress200.photique.user.domain.dto.UserInfoResponse;
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
        String imageUrl = null;

        if (joinRequest.hasProfileImage()) {
            imageUrl = imageUploader.upload(joinRequest.getProfileImage(), profileImagePath);
        }

        User user = joinRequest.toEntity(password, imageUrl);

        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new UserException("Email or nickname already exists", e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @Override
    public UserInfoResponse getUserInfo(final Long userId) {
        User foundUser = findUserById(userId);
        return UserInfoResponse.from(foundUser);
    }

    @Override
    public void updateUserInfo(
            final Long userId,
            final UpdateUserRequest updateUserRequest
    ) {
        User user = findUserById(userId);

        if (updateUserRequest.hasPassword()) {
            updatePassword(user, updateUserRequest.getPassword());
        }

        if (updateUserRequest.hasNickname()) {
            updateNickname(user, updateUserRequest.getNickname());
        }

        if (updateUserRequest.isIntroductionDefault()) {
            user.updateIntroduction(null);
        } else if (updateUserRequest.hasIntroduction()) { // 기본값 설정이 아니고 수정요청 값이 존재한다면
            updateIntroduction(user, updateUserRequest.getIntroduction());
        }

        if (updateUserRequest.isProfileImageDefault()) {
            String profileImage = user.getProfileImage();

            if (profileImage != null) {
                imageUploader.delete(profileImage);
                user.updateProfileImage(null);
            }

        } else if (updateUserRequest.hasProfileImage()) {
            String profileImage = user.getProfileImage();

            profileImage = imageUploader.update(
                    updateUserRequest.getProfileImage(),
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
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserException("User with ID {" + userId + "} is not found", HttpStatus.NOT_FOUND));

        userRepository.deleteById(userId);
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
