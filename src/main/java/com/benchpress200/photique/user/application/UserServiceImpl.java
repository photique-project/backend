package com.benchpress200.photique.user.application;

import com.benchpress200.photique.common.infrastructure.ImageUploader;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.dto.JoinRequest;
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
public class UserServiceImpl implements UserService{
    @Value("${cloud.aws.s3.path.profile}")
    private String profileImagePath;

    private final ImageUploader imageUploader;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public void join(final JoinRequest joinRequest) {
        // 1. 비밀번호 암호화
        String password = passwordEncoder.encode(joinRequest.getPassword());

        // 2. 사진데이터 S3에 업로드하고 url 반환
        String imageUrl = null;

        if (joinRequest.hasProfileImage()) {
            imageUrl = imageUploader.upload(joinRequest.getProfileImage(), profileImagePath);
        }

        // 3. 엔티티 생성
        User user = joinRequest.toEntity(password, imageUrl);

        // 4. 엔티티 저장
        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new UserException("Email or nickname already exists", e.getMessage(), HttpStatus.CONFLICT);
        }
    }
}
