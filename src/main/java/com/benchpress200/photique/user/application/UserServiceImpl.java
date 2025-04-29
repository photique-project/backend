package com.benchpress200.photique.user.application;

import com.benchpress200.photique.auth.domain.AuthDomainService;
import com.benchpress200.photique.exhibition.domain.ExhibitionCommentDomainService;
import com.benchpress200.photique.exhibition.domain.ExhibitionDomainService;
import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionWork;
import com.benchpress200.photique.image.domain.ImageDomainService;
import com.benchpress200.photique.singlework.domain.SingleWorkCommentDomainService;
import com.benchpress200.photique.singlework.domain.SingleWorkDomainService;
import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.user.application.cache.UserCacheService;
import com.benchpress200.photique.user.domain.FollowDomainService;
import com.benchpress200.photique.user.domain.UserDomainService;
import com.benchpress200.photique.user.domain.dto.JoinRequest;
import com.benchpress200.photique.user.domain.dto.NicknameValidationRequest;
import com.benchpress200.photique.user.domain.dto.ResetPasswordRequest;
import com.benchpress200.photique.user.domain.dto.UserDetailsRequest;
import com.benchpress200.photique.user.domain.dto.UserDetailsResponse;
import com.benchpress200.photique.user.domain.dto.UserSearchRequest;
import com.benchpress200.photique.user.domain.dto.UserSearchResponse;
import com.benchpress200.photique.user.domain.dto.UserUpdateRequest;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.entity.UserSearch;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
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
    private final FollowDomainService followDomainService;
    private final AuthDomainService authDomainService;
    private final ImageDomainService imageDomainService;
    private final SingleWorkDomainService singleWorkDomainService;
    private final SingleWorkCommentDomainService singleWorkCommentDomainService;
    private final ExhibitionDomainService exhibitionDomainService;
    private final ExhibitionCommentDomainService exhibitionCommentDomainService;
    private final UserCacheService userCacheService;

    @Override
    public void validateNickname(final NicknameValidationRequest nicknameValidationRequest) {
        // 닉네임 중복검사 수행
        String nickname = nicknameValidationRequest.getNickname();
        userDomainService.isDuplicatedNickname(nickname);
    }

    @Override
    @Transactional
    public void join(final JoinRequest joinRequest) {
        // 이메일 인증 완료 여부 확인
        String email = joinRequest.getEmail();
        authDomainService.isValidUser(email);

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
    @Transactional
    public UserDetailsResponse getUserDetails(final UserDetailsRequest userDetailsRequest) {
        // 캐시 데이터 우선 조회 서비스 호출
        UserDetailsResponse userDetailsResponse = userCacheService.getUserDetails(userDetailsRequest);

        // 요청유저의 팔로우 여부 조회
        // 각각의 요청 유저마다 팔로잉 상태는 다르므로 해당 값은 캐싱에서 제외
        long userId = userDetailsRequest.getUserId();
        long requestUserId = userDetailsRequest.getRequestUserId();
        boolean isFollowing = followDomainService.isFollowing(requestUserId, userId);

        // 팔로잉 여부 할당
        userDetailsResponse.updateFollowingStatus(isFollowing);

        // 유저상세조회 dto 반환
        return userDetailsResponse;
    }

    @Override
    @Transactional
    @CacheEvict(
            value = "userDetails",
            key = "#userUpdateRequest.userId"
    )
    public void updateUserDetails(final UserUpdateRequest userUpdateRequest) {
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

        // 업데이트 시간 마킹
        userDomainService.markAsUpdated(user);
    }

    @Override
    @Transactional
    public Page<UserSearchResponse> searchUsers(
            final UserSearchRequest userSearchRequest,
            final Pageable pageable
    ) {
        // 키워드 조회
        String keyword = userSearchRequest.getKeyword();
        Page<UserSearch> userSearchPage = userDomainService.searchUsers(keyword, pageable);

        // 검색 유저중 팔로우 상태 조회
        Long userId = userSearchRequest.getUserId();

        List<UserSearchResponse> userSearchResponseList = userSearchPage.stream()
                .map(userSearch -> {
                    boolean isFollowing = followDomainService.isFollowing(userId, userSearch.getId());
                    return UserSearchResponse.of(userSearch, isFollowing);
                })
                .toList();

        return new PageImpl<>(userSearchResponseList, pageable, userSearchPage.getTotalElements());
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "userDetails", key = "#userId"),
            @CacheEvict(value = "searchSingleWorkPage", allEntries = true),
            @CacheEvict(value = "searchExhibitionPage", allEntries = true)
    })
    public void withdraw(final Long userId) {
        // 유저 조회
        User user = userDomainService.findUser(userId);

        // 유저 이미지 삭제
        String profileImage = user.getProfileImage();
        imageDomainService.delete(profileImage);

        // 유저작성 단일작품 조회
        List<SingleWork> singleWorks = singleWorkDomainService.findSingleWork(user);

        // 단일작품 이미지 삭제
        List<String> singleWorksImage = singleWorks.stream().
                map(SingleWork::getImage)
                .toList();
        singleWorksImage.forEach(imageDomainService::delete);

        // 단일작품 좋아요 데이터는 두 테이블을 참조하기 때문에 따로 삭제
        singleWorks.forEach(singleWorkDomainService::deleteLike);

        // 마찬가지로 단일작품 댓글도 두 테이블을 참조하기 때문에 따로 삭제
        singleWorks.forEach(singleWorkCommentDomainService::deleteComment);

        // 단일작품 이미지 따로 삭제했으면 단일작품 삭제하면 나머지 cascade 로 모두삭제됨
        singleWorks.forEach(singleWorkDomainService::deleteSingleWork);

        // 유저작성 전시회 조회
        List<Exhibition> exhibitions = exhibitionDomainService.findExhibition(user);

        // 전시회 순회하면서 작품 리스트 찾고 이미지 딜리트
        exhibitions.forEach(exhibition -> {
            List<ExhibitionWork> exhibitionWorks = exhibitionDomainService.findExhibitionWork(exhibition);
            exhibitionWorks.forEach(exhibitionWork -> imageDomainService.delete(exhibitionWork.getImage()));
            exhibitionDomainService.deleteExhibitionWork(exhibition);
        });

        // 전시회 좋아요 데이터는 두 테이블을 참조하기 때문에 따로 삭제
        exhibitions.forEach(exhibitionDomainService::deleteLike);

        // 전시회 북마크 데이터는 두 테이블을 참조하기 때문에 따로 삭제
        exhibitions.forEach(exhibitionDomainService::deleteBookmark);

        // 전시회 댓글 데이터는 두 테이블을 참조하기 때문에 따로 삭제
        exhibitions.forEach(exhibitionCommentDomainService::deleteComment);

        // 전시회 삭제 - cascade
        exhibitions.forEach(exhibitionDomainService::deleteExhibition);

        // 유저가 작성한 단일작품, 전시회 좋아요, 북마크, 댓글 삭제
        singleWorkDomainService.deleteLike(user);
        singleWorkCommentDomainService.deleteComment(user);

        exhibitionDomainService.deleteLike(user);
        exhibitionDomainService.deleteBookmark(user);
        exhibitionCommentDomainService.deleteComment(user);

        // 유저관련 팔로우 팔로잉 데이터 삭제
        // 유저 엔티티전달하면 팔로워 랄로잉모두속하는거삭제
        followDomainService.deleteFollow(user);

        // 유저삭제
        userDomainService.deleteUser(user);
    }

    @Override
    @Transactional
    public void resetPassword(final ResetPasswordRequest resetPasswordRequest) {
        // 이메일 인증완료 되었는지 확인
        String email = resetPasswordRequest.getEmail();
        authDomainService.isValidUser(email);

        // 유저조회
        User user = userDomainService.findUser(email);

        // 비밀번호 업데이트
        String newPassword = resetPasswordRequest.getPassword();
        userDomainService.updatePassword(user, newPassword);
    }
}
