package com.benchpress200.photique.user.application;

import com.benchpress200.photique.auth.infrastructure.TokenManager;
import com.benchpress200.photique.image.domain.ImageDomainService;
import com.benchpress200.photique.image.infrastructure.ImageUploader;
import com.benchpress200.photique.user.domain.UserDomainService;
import com.benchpress200.photique.user.domain.dto.JoinRequest;
import com.benchpress200.photique.user.domain.dto.NicknameValidationRequest;
import com.benchpress200.photique.user.domain.dto.UserDetailResponse;
import com.benchpress200.photique.user.domain.dto.UserIdResponse;
import com.benchpress200.photique.user.domain.dto.UserSearchRequest;
import com.benchpress200.photique.user.domain.dto.UserSearchResponse;
import com.benchpress200.photique.user.domain.dto.UserUpdateRequest;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.entity.UserSearch;
import com.benchpress200.photique.user.infrastructure.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Value("${cloud.aws.s3.path.profile}")
    private String profileImagePath;

    private final UserDomainService userDomainService;
    private final ImageDomainService imageDomainService;

    private final ImageUploader imageUploader;
    private final UserRepository userRepository;
    private final TokenManager tokenManager;

    @Override
    public void validateNickname(final NicknameValidationRequest nicknameValidationRequest) {
        // 닉네임 중복검사 수행
        String nickname = nicknameValidationRequest.getNickname();
        userDomainService.isDuplicatedNickname(nickname);
    }

    @Override
    @Transactional
    public void join(final JoinRequest joinRequest) {
        // 비밀번호 인코딩
        String rawPassword = joinRequest.getPassword();
        String encodedPassword = userDomainService.encodePassword(rawPassword);

        // 이미지 S3 업로드
        MultipartFile newImage = joinRequest.getProfileImage();
        String uploadedImageUrl = imageDomainService.upload(newImage, profileImagePath);

        // 새 유저 생성
        User user = joinRequest.toEntity(encodedPassword, uploadedImageUrl);
        userDomainService.registerUser(user);
    }

    @Override
    public UserDetailResponse getUserDetail(final Long userId) {
        // 유저 데이터 조회
        User foundUser = userDomainService.findUser(userId);

        // 응답 데이터로 변환 후 반환
        return UserDetailResponse.from(foundUser);
    }

    @Override
    @Transactional
    public void updateUserDetail(
            final UserUpdateRequest userUpdateRequest
    ) {
        // 유저 조회
        Long userId = userUpdateRequest.getUserId();
        User user = userDomainService.findUser(userId);

        // 비밀번호 업데이트
        String newPassword = userUpdateRequest.getPassword();
        userDomainService.updatePassword(user, newPassword);

        // 닉네임 업데이트
        String newNickname = userUpdateRequest.getNickname();
        userDomainService.updateNickname(user, newNickname);

        // 한 줄 소개 업데이트
        String newIntroduction = userUpdateRequest.getIntroduction();
        userDomainService.updateIntroduction(user, newIntroduction);

        // 프로필 이미지 업데이트
        String oldPath = user.getProfileImage();
        MultipartFile newProfileImage = userUpdateRequest.getProfileImage();
        String updatedProfileImageUrl = imageDomainService.update(newProfileImage, oldPath, profileImagePath);
        userDomainService.updateProfileImage(user, updatedProfileImageUrl);
    }

    @Override
    public Page<UserSearchResponse> searchUsers(
            final UserSearchRequest userSearchRequest,
            final Pageable pageable
    ) {
        String keyword = userSearchRequest.getKeyword();
        Page<UserSearch> userSearchPage = userDomainService.searchUsers(keyword, pageable);
        List<UserSearchResponse> userSearchResponsesList = userSearchPage.stream()
                .map(UserSearchResponse::from)
                .toList();

        return new PageImpl<>(userSearchResponsesList, pageable, userSearchPage.getTotalElements());
    }


    // TODO: 이후에 해당 유저 관련 데이터 모두 삭제하는 코드 추가해야함, 팔로우 기능 추가하면 팔로우데이터, 알림데이터도 모두 삭제
    @Override
    @Transactional
    public void withdraw(final Long userId) {
        // 유저 조회
        User user = userDomainService.findUser(userId);
        // 단일작품 댓글
        // 단일작품 좋아요
        // 단일작품 태그
        // 단일작품
        // 전시회 댓글
        // 전시회 좋아요
        // 전시회 북마크
        // 단일작품 태그
        // 단일작품
        // 알림
        // 팔로우
        // 유저삭제

        userRepository.deleteById(userId);
    }

    @Override
    public UserIdResponse getUserId(final String accessToken) {

        Long userId = tokenManager.getUserId(accessToken);
        return UserIdResponse.builder()
                .id(userId)
                .build();
    }

}
