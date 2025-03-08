package com.benchpress200.photique.user.application;

import com.benchpress200.photique.auth.infrastructure.TokenManager;
import com.benchpress200.photique.exhibition.domain.ExhibitionCommentDomainService;
import com.benchpress200.photique.exhibition.domain.ExhibitionDomainService;
import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionWork;
import com.benchpress200.photique.image.domain.ImageDomainService;
import com.benchpress200.photique.singlework.domain.SingleWorkCommentDomainService;
import com.benchpress200.photique.singlework.domain.SingleWorkDomainService;
import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.user.domain.FollowDomainService;
import com.benchpress200.photique.user.domain.UserDomainService;
import com.benchpress200.photique.user.domain.dto.JoinRequest;
import com.benchpress200.photique.user.domain.dto.NicknameValidationRequest;
import com.benchpress200.photique.user.domain.dto.UserDetailResponse;
import com.benchpress200.photique.user.domain.dto.UserSearchRequest;
import com.benchpress200.photique.user.domain.dto.UserSearchResponse;
import com.benchpress200.photique.user.domain.dto.UserUpdateRequest;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.entity.UserSearch;
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

    private final ImageDomainService imageDomainService;
    private final UserDomainService userDomainService;
    private final SingleWorkDomainService singleWorkDomainService;
    private final SingleWorkCommentDomainService singleWorkCommentDomainService;
    private final ExhibitionDomainService exhibitionDomainService;
    private final ExhibitionCommentDomainService exhibitionCommentDomainService;
    private final FollowDomainService followDomainService;

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

        // 유저 단일작품 개수 조회
        Long singleWorkCount = singleWorkDomainService.countSingleWork(foundUser);

        // 유저 전시회 개수 조회
        Long exhibitionCount = exhibitionDomainService.countExhibition(foundUser);

        // 유저 팔로워 수 조회
        Long followerCount = followDomainService.countFollowers(foundUser);

        // 유저 팔로잉 수 조회
        Long followingCount = followDomainService.countFollowings(foundUser);

        // 응답 데이터로 변환 후 반환
        return UserDetailResponse.of(
                foundUser,
                singleWorkCount,
                exhibitionCount,
                followerCount,
                followingCount
        );
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

    @Override
    @Transactional
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
}
