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
import java.util.Optional;
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
        Optional<User> user = userRepository.findById(userId);
        User foundUser = user.orElseThrow(
                () -> new UserException("User with ID {" + userId + "} is not found", HttpStatus.NOT_FOUND));

        return UserInfoResponse.from(foundUser);
    }

    @Override
    public void updateUserInfo(
            final Long userId,
            final UpdateUserRequest updateUserRequest
    ) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserException("User with ID {" + userId + "} is not found", HttpStatus.NOT_FOUND));

        if (updateUserRequest.hasPassword()) {
            user.updatePassword(passwordEncoder.encode(updateUserRequest.getPassword()));
        }

        if (updateUserRequest.hasNickname()) {
            userRepository.findByNickname(updateUserRequest.getNickname())
                    .ifPresent(foundUser -> {
                        throw new UserException("{" + updateUserRequest.getNickname() + "} is already in use",
                                HttpStatus.CONFLICT);
                    });

            user.updateNickname(updateUserRequest.getNickname());
        }

        if (updateUserRequest.hasProfileImage()) {
            String profileImage = user.getProfileImage();
            if (profileImage != null) {
                imageUploader.delete(profileImage);
            }

            profileImage = imageUploader.update(
                    updateUserRequest.getProfileImage(),
                    profileImage,
                    profileImagePath
            );

            user.updateProfileImage(profileImage);

            return;
        }

        if (updateUserRequest.isProfileImageDefault()) {
            String profileImage = user.getProfileImage();

            if (profileImage != null) {
                imageUploader.delete(profileImage);
            }

            user.updateProfileImage(null);
        }
    }

    @Override
    public UserIdResponse getUserId(String accessToken) {
        Long userId = tokenManager.getUserId(accessToken);
        return UserIdResponse.builder()
                .id(userId)
                .build();
    }

    //FIXME: 이후에 해당 유저 관련 데이터 모두 삭제하는 코드 추가해야함
    @Override
    public void withdraw(Long userId) {
        // 유저있는지확인
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserException("User with ID {" + userId + "} is not found", HttpStatus.NOT_FOUND));

        userRepository.deleteById(userId);
    }
}
