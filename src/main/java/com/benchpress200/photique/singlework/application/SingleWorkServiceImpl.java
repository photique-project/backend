package com.benchpress200.photique.singlework.application;


import com.benchpress200.photique.common.dto.RestPage;
import com.benchpress200.photique.image.domain.ImageDomainService;
import com.benchpress200.photique.notification.domain.NotificationDomainService;
import com.benchpress200.photique.notification.domain.entity.Notification;
import com.benchpress200.photique.notification.domain.enumeration.Type;
import com.benchpress200.photique.singlework.application.cache.SingleWorkCacheService;
import com.benchpress200.photique.singlework.domain.SingleWorkCommentDomainService;
import com.benchpress200.photique.singlework.domain.SingleWorkDomainService;
import com.benchpress200.photique.singlework.domain.dto.LikedSingleWorkRequest;
import com.benchpress200.photique.singlework.domain.dto.LikedSingleWorkResponse;
import com.benchpress200.photique.singlework.domain.dto.MySingleWorkRequest;
import com.benchpress200.photique.singlework.domain.dto.MySingleWorkResponse;
import com.benchpress200.photique.singlework.domain.dto.SingleWorkCreateRequest;
import com.benchpress200.photique.singlework.domain.dto.SingleWorkDetailRequest;
import com.benchpress200.photique.singlework.domain.dto.SingleWorkDetailResponse;
import com.benchpress200.photique.singlework.domain.dto.SingleWorkLikeDecrementRequest;
import com.benchpress200.photique.singlework.domain.dto.SingleWorkLikeIncrementRequest;
import com.benchpress200.photique.singlework.domain.dto.SingleWorkSearchRequest;
import com.benchpress200.photique.singlework.domain.dto.SingleWorkSearchResponse;
import com.benchpress200.photique.singlework.domain.dto.SingleWorkUpdateRequest;
import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkLike;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkSearch;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkTag;
import com.benchpress200.photique.tag.domain.TagDomainService;
import com.benchpress200.photique.tag.domain.entity.Tag;
import com.benchpress200.photique.user.domain.FollowDomainService;
import com.benchpress200.photique.user.domain.UserDomainService;
import com.benchpress200.photique.user.domain.entity.Follow;
import com.benchpress200.photique.user.domain.entity.User;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class SingleWorkServiceImpl implements SingleWorkService {
    @Value("${cloud.aws.s3.path.single-work}")
    private String imagePath;

    private final ImageDomainService imageDomainService;
    private final UserDomainService userDomainService;
    private final SingleWorkDomainService singleWorkDomainService;
    private final SingleWorkCommentDomainService singleWorkCommentDomainService;
    private final TagDomainService tagDomainService;
    private final NotificationDomainService notificationDomainService;
    private final FollowDomainService followDomainService;
    private final SingleWorkCacheService singleWorkCacheService;


    @Override
    @Transactional
    @CacheEvict(
            value = "searchSingleWorkPage",
            allEntries = true
    )
    public void postNewSingleWork(final SingleWorkCreateRequest singleWorkCreateRequest) {
        // 작성자 조회
        Long writerId = singleWorkCreateRequest.getWriterId();
        User writer = userDomainService.findUser(writerId);

        // 이미지 업로드 & 단일작품 저장
        MultipartFile image = singleWorkCreateRequest.getImage();
        String imageUrl = imageDomainService.upload(image, imagePath);

        // 일단 단일작품만 저장
        SingleWork singleWork = singleWorkCreateRequest.toSingleWorkEntity(writer, imageUrl);
        singleWork = singleWorkDomainService.createNewSingleWork(singleWork);

        // 일단 태그를 엔티티로 꺼내서 태그도메인서비스로 존재하지않던것들은 구분해서 새로저장
        List<String> tagNames = singleWorkCreateRequest.getTags();
        List<Tag> tags = tagDomainService.createNewTags(tagNames);

        // 그 다음 단일작품 태그 엔티티로 꺼내서 저장
        List<SingleWorkTag> singleWorkTags = singleWorkCreateRequest.toSingleWorkTagEntities(singleWork, tags);
        singleWorkDomainService.createNewSingleWorkTags(singleWorkTags);

        // 엘라스틱 서치 저장
        SingleWorkSearch singleWorkSearch = SingleWorkSearch.of(singleWork, writer, tagNames);
        singleWorkDomainService.createNewSingleWorkSearch(singleWorkSearch);

        // 알림생성
        Long singleWorkId = singleWork.getId();
        List<Follow> follows = followDomainService.getFollowers(writer);// 보낼 대상은 팔로우들
        follows.forEach((follow) -> {
            User follower = follow.getFollower();
            Notification notification = Notification.builder()
                    .user(follower)
                    .type(Type.FOLLOWING_SINGLE_WORK)
                    .targetId(singleWorkId)
                    .build();

            // 알림 데이터 비동기 생성
            notificationDomainService.createNotification(notification);

            // 알림 비동기 처리
            Long followerId = follow.getId();
            notificationDomainService.pushNewNotification(followerId);
        });
    }


    @Override
    @Transactional
    public SingleWorkDetailResponse getSingleWorkDetails(final SingleWorkDetailRequest singleWorkDetailRequest) {
        // 단일작품 조회
        Long singleWorkId = singleWorkDetailRequest.getSingleWorkId();
        SingleWork singleWork = singleWorkDomainService.findSingleWorkWithWriter(singleWorkId);

        // 조회수 증가
        singleWorkDomainService.incrementView(singleWork);

        // 업데이트 마킹
        singleWorkDomainService.markAsUpdated(singleWork);

        // 단일작품 태그 리스트 조회
        List<SingleWorkTag> singleWorkTags = singleWorkDomainService.findSingleWorkTagWithTag(singleWork);

        // 단일작품 태그와 매칭되는 태그 조회
        List<Tag> tags = singleWorkTags.stream()
                .map(SingleWorkTag::getTag)
                .toList();

        // 단일작품 좋아요 수 조회
        Long likeCount = singleWorkDomainService.countLike(singleWork);

        // 요청한 유저의 작품 좋아요 유무
        Long userId = singleWorkDetailRequest.getUserId();
        boolean isLiked = singleWorkDomainService.isLiked(userId, singleWorkId);

        // 요청한 유저의 해당 작품 작가 팔로잉 유무
        boolean isFollowing = followDomainService.isFollowing(userId, singleWork.getWriter().getId());

        return SingleWorkDetailResponse.of(
                singleWork,
                tags,
                likeCount,
                isLiked,
                isFollowing
        );
    }

    @Override
    @Transactional
    public Page<SingleWorkSearchResponse> searchSingleWorks(
            final SingleWorkSearchRequest singleWorkSearchRequest,
            final Pageable pageable
    ) {
        // 단일작품 검색
        Page<SingleWorkSearch> singleWorkSearchPage = singleWorkCacheService.searchSingleWorks(singleWorkSearchRequest,
                pageable);

        // 단일작품 중에서 유저아이디에 해당하는 좋아요 작품 선별해서 좋아요 담기
        Long userId = singleWorkSearchRequest.getUserId();

        // 검섹페이지에 있는 단일작품 id 리스트 가져오기
        List<Long> singleWorkIds = singleWorkSearchPage.stream()
                .map(SingleWorkSearch::getId)
                .toList();

        // 한 번의 쿼리로 요청 유저가 좋아요한 작품 id를 HashSet 으로 반환
        Set<Long> likedSingleWorkIds = singleWorkDomainService.findLikedSingleWorkIds(userId, singleWorkIds);

        List<SingleWorkSearchResponse> singleWorkSearchResponsePage = singleWorkSearchPage.stream()
                .map(singleWorkSearch -> {
                    long singleWorkId = singleWorkSearch.getId();
                    boolean isLiked = likedSingleWorkIds.contains(singleWorkId);
                    return SingleWorkSearchResponse.of(singleWorkSearch, isLiked);
                })
                .toList();

        return new RestPage<>(singleWorkSearchResponsePage, pageable, singleWorkSearchPage.getTotalElements());
    }


    @Override
    @Transactional
    public void updateSingleWorkDetails(final SingleWorkUpdateRequest singleWorkUpdateRequest) {
        // 단일작품 조회
        Long singleWorkId = singleWorkUpdateRequest.getId();
        SingleWork singleWork = singleWorkDomainService.findSingleWork(singleWorkId);

        // 카메라 업데이트
        String newCamera = singleWorkUpdateRequest.getCamera();
        singleWorkDomainService.updateCamera(singleWork, newCamera);

        // 렌즈 업데이트
        String newLens = singleWorkUpdateRequest.getLens();
        singleWorkDomainService.updateLens(singleWork, newLens);

        // 조리개 값 업데이트
        String newAperture = singleWorkUpdateRequest.getAperture();
        singleWorkDomainService.updateAperture(singleWork, newAperture);

        // 셔터스피드 업데이트
        String newShutterSpeed = singleWorkUpdateRequest.getShutterSpeed();
        singleWorkDomainService.updateShutterSpeed(singleWork, newShutterSpeed);

        // ISO 업데이트
        String newIso = singleWorkUpdateRequest.getIso();
        singleWorkDomainService.updateIso(singleWork, newIso);

        // 위치 업데이트
        String newLocation = singleWorkUpdateRequest.getLocation();
        singleWorkDomainService.updateLocation(singleWork, newLocation);

        // 카테고리 업데이트
        String newCategory = singleWorkUpdateRequest.getCategory();
        singleWorkDomainService.updateCategory(singleWork, newCategory);

        // 날짜 업데이트
        LocalDate newDate = singleWorkUpdateRequest.getDate();
        singleWorkDomainService.updateDate(singleWork, newDate);

        // 태그 업데이트
        List<String> newTagNames = singleWorkUpdateRequest.getTags();
        List<Tag> tags = tagDomainService.createNewTags(newTagNames);
        singleWorkDomainService.updateTags(singleWork, tags);

        // 타이틀 업데이트
        String newTitle = singleWorkUpdateRequest.getTitle();
        singleWorkDomainService.updateTitle(singleWork, newTitle);

        // 설명 업데이트
        String newDescription = singleWorkUpdateRequest.getDescription();
        singleWorkDomainService.updateDescription(singleWork, newDescription);

        // 업데이트 마킹
        singleWorkDomainService.markAsUpdated(singleWork);
    }

    @Override
    @Transactional
    @CacheEvict(
            value = "searchSingleWorkPage",
            allEntries = true
    )
    public void removeSingleWork(final Long singleworkId) {
        SingleWork singleWork = singleWorkDomainService.findSingleWork(singleworkId);

        // s3 이미지 삭제
        String imageUrl = singleWork.getImage();
        imageDomainService.delete(imageUrl);

        // 단일작품 좋아요 삭제
        singleWorkDomainService.deleteSingleWorkLike(singleWork);

        // 단일작품 태그 삭제
        singleWorkDomainService.deleteSingleWorkTag(singleWork);

        // 단일작품 댓글 삭제
        singleWorkCommentDomainService.deleteComment(singleWork);

        // 단일작품 삭제
        singleWorkDomainService.deleteSingleWork(singleWork);
    }

    @Override
    @Transactional
    public void incrementLike(final SingleWorkLikeIncrementRequest singleWorkLikeIncrementRequest) {
        // 유저존재확인
        Long userId = singleWorkLikeIncrementRequest.getUserId();
        User user = userDomainService.findUser(userId);

        // 작품존재확인
        Long singleWorkId = singleWorkLikeIncrementRequest.getSingleWorkId();
        SingleWork singleWork = singleWorkDomainService.findSingleWork(singleWorkId);

        // 이미 좋아요 추가헀는지 확인
        singleWorkDomainService.isAlreadyLiked(user, singleWork);

        // 작품 좋아요 추가
        SingleWorkLike singleWorkLike = singleWorkLikeIncrementRequest.toEntity(user, singleWork);
        singleWorkDomainService.incrementLike(singleWorkLike);

        // 알림생성
        Long singleWorkWriterId = singleWork.getWriter().getId();
        User singleWorkWriter = userDomainService.findUser(singleWorkWriterId);

        Notification notification = Notification.builder()
                .user(singleWorkWriter)
                .type(Type.SINGLE_WORK_LIKE)
                .targetId(singleWorkId)
                .build();

        // 알림 데이터 비동기 생성
        notificationDomainService.createNotification(notification);

        // 알림 비동기 처리
        notificationDomainService.pushNewNotification(singleWorkWriterId);

        // 업데이트 마킹
        singleWorkDomainService.markAsUpdated(singleWork);
    }

    @Override
    @Transactional
    public void decrementLike(final SingleWorkLikeDecrementRequest singleWorkLikeDecrementRequest) {
        // 유저존재확인
        Long userId = singleWorkLikeDecrementRequest.getUserId();
        User user = userDomainService.findUser(userId);

        // 작품존재확인
        Long singleWorkId = singleWorkLikeDecrementRequest.getSingleWorkId();
        SingleWork singleWork = singleWorkDomainService.findSingleWork(singleWorkId);

        // 작품 좋아요 삭제
        singleWorkDomainService.decrementLike(user, singleWork);

        // 업데이트 마킹
        singleWorkDomainService.markAsUpdated(singleWork);
    }

    @Override
    @Transactional
    public Page<LikedSingleWorkResponse> getLikedSingleWorks(
            final LikedSingleWorkRequest likedSingleWorkRequest,
            final Pageable pageable
    ) {
        Long userId = likedSingleWorkRequest.getUserId();

        Page<SingleWorkSearch> likedSingleWorkSearchPage = singleWorkDomainService.findLikedSingleWorksByUser(
                userId,
                pageable
        );

        List<LikedSingleWorkResponse> likedSingleWorkResponsePage = likedSingleWorkSearchPage.stream()
                .map(LikedSingleWorkResponse::from)
                .toList();

        return new PageImpl<>(likedSingleWorkResponsePage, pageable, likedSingleWorkSearchPage.getTotalElements());
    }

    @Override
    @Transactional
    public Page<MySingleWorkResponse> getMySingleWorks(
            final MySingleWorkRequest mySingleWorkRequest,
            final Pageable pageable
    ) {
        Long userId = mySingleWorkRequest.getUserId();

        // 나의 단일작품 조회
        Page<SingleWorkSearch> mySingleWorkSearchpage = singleWorkDomainService.findMySingleWorkByUser(
                userId,
                pageable
        );

        // 좋아요 여부 조회
        List<SingleWorkLike> singleWorkLikes = singleWorkDomainService.findLikeByUser(userId);

        // 페이지 반환
        List<MySingleWorkResponse> singleWorkSearchResponsePage = mySingleWorkSearchpage.stream()
                .map(singleWorkSearch -> {
                    boolean isLiked = singleWorkLikes.stream()
                            .anyMatch(like -> like.getSingleWork().getId().equals(singleWorkSearch.getId()));

                    return MySingleWorkResponse.of(singleWorkSearch, isLiked);
                })
                .toList();

        return new PageImpl<>(singleWorkSearchResponsePage, pageable, mySingleWorkSearchpage.getTotalElements());
    }
}
