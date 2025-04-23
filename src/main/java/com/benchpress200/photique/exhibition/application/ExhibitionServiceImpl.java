package com.benchpress200.photique.exhibition.application;

import com.benchpress200.photique.common.dto.RestPage;
import com.benchpress200.photique.exhibition.domain.ExhibitionCommentDomainService;
import com.benchpress200.photique.exhibition.domain.ExhibitionDomainService;
import com.benchpress200.photique.exhibition.domain.dto.BookmarkedExhibitionRequest;
import com.benchpress200.photique.exhibition.domain.dto.BookmarkedExhibitionResponse;
import com.benchpress200.photique.exhibition.domain.dto.ExhibitionBookmarkRemoveRequest;
import com.benchpress200.photique.exhibition.domain.dto.ExhibitionBookmarkRequest;
import com.benchpress200.photique.exhibition.domain.dto.ExhibitionCreateRequest;
import com.benchpress200.photique.exhibition.domain.dto.ExhibitionDetailsRequest;
import com.benchpress200.photique.exhibition.domain.dto.ExhibitionDetailsResponse;
import com.benchpress200.photique.exhibition.domain.dto.ExhibitionLikeDecrementRequest;
import com.benchpress200.photique.exhibition.domain.dto.ExhibitionLikeIncrementRequest;
import com.benchpress200.photique.exhibition.domain.dto.ExhibitionSearchRequest;
import com.benchpress200.photique.exhibition.domain.dto.ExhibitionSearchResponse;
import com.benchpress200.photique.exhibition.domain.dto.ExhibitionWorkCreateRequest;
import com.benchpress200.photique.exhibition.domain.dto.LikedExhibitionRequest;
import com.benchpress200.photique.exhibition.domain.dto.LikedExhibitionResponse;
import com.benchpress200.photique.exhibition.domain.dto.MyExhibitionRequest;
import com.benchpress200.photique.exhibition.domain.dto.MyExhibitionResponse;
import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionBookmark;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionLike;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionSearch;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionTag;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionWork;
import com.benchpress200.photique.image.domain.ImageDomainService;
import com.benchpress200.photique.notification.domain.NotificationDomainService;
import com.benchpress200.photique.notification.domain.entity.Notification;
import com.benchpress200.photique.notification.domain.enumeration.Type;
import com.benchpress200.photique.singlework.domain.enumeration.Target;
import com.benchpress200.photique.tag.domain.TagDomainService;
import com.benchpress200.photique.tag.domain.entity.Tag;
import com.benchpress200.photique.user.domain.FollowDomainService;
import com.benchpress200.photique.user.domain.UserDomainService;
import com.benchpress200.photique.user.domain.entity.Follow;
import com.benchpress200.photique.user.domain.entity.User;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class ExhibitionServiceImpl implements ExhibitionService {
    @Value("${cloud.aws.s3.path.exhibition}")
    private String imagePath;

    private final ImageDomainService imageDomainService;
    private final UserDomainService userDomainService;
    private final ExhibitionDomainService exhibitionDomainService;
    private final ExhibitionCommentDomainService exhibitionCommentDomainService;
    private final TagDomainService tagDomainService;
    private final NotificationDomainService notificationDomainService;
    private final FollowDomainService followDomainService;

    @Override
    @Transactional
    public void holdNewExhibition(final ExhibitionCreateRequest exhibitionCreateRequest) {
        // 작가 조회
        Long writerId = exhibitionCreateRequest.getWriterId();
        User writer = userDomainService.findUser(writerId);

        // 워크 리스트 순회하면서 유저 조회 하고 이미지 변환하고 전시회 작품 엔티티 저장
        Exhibition exhibition = exhibitionCreateRequest.toEntity(writer);
        Exhibition savedExhibition = exhibitionDomainService.createNewExhibition(exhibition);

        // 전시회 개별 작품 저장
        List<ExhibitionWorkCreateRequest> works = exhibitionCreateRequest.getWorks();
        List<ExhibitionWork> exhibitionWorks = works.stream()
                .map(work -> {
                    MultipartFile image = work.getImage();
                    String imageUrl = imageDomainService.upload(image, imagePath);
                    return work.toEntity(savedExhibition, imageUrl);
                })
                .toList();

        exhibitionDomainService.createNewExhibitionWorks(exhibitionWorks);

        // 태그 저장
        List<String> tagNames = exhibitionCreateRequest.getTags();
        List<Tag> tags = tagDomainService.createNewTags(tagNames);
        List<ExhibitionTag> exhibitionTags = exhibitionCreateRequest.toExhibitionTagEntities(savedExhibition, tags);
        exhibitionDomainService.createNewExhibitionTags(exhibitionTags);

        // 엘라스틱 서치 저장
        ExhibitionSearch exhibitionSearch = ExhibitionSearch.of(exhibition, writer, tagNames);
        exhibitionDomainService.createNewExhibitionSearch(exhibitionSearch);

        // 알림생성
        Long exhibitionId = exhibition.getId();
        List<Follow> follows = followDomainService.getFollowers(writer);// 보낼 대상은 팔로워들
        follows.forEach((follow) -> {
            User follower = follow.getFollower();
            Notification notification = Notification.builder()
                    .user(follower)
                    .type(Type.FOLLOWING_EXHIBITION)
                    .targetId(exhibitionId)
                    .build();

            notification = notificationDomainService.createNotification(notification);

            // 알림 비동기 처리
            Long followerId = follow.getId();
            Long notificationId = notification.getId();
            notificationDomainService.pushNewNotification(followerId, notificationId);
        });
    }


    @Override
    @Transactional
    public ExhibitionDetailsResponse getExhibitionDetails(final ExhibitionDetailsRequest exhibitionDetailsRequest) {

        // 전시회 조회
        Long exhibitionId = exhibitionDetailsRequest.getExhibitionId();
        Exhibition exhibition = exhibitionDomainService.findExhibition(exhibitionId);

        // 전시회 개별작품 조회
        List<ExhibitionWork> exhibitionWorks = exhibitionDomainService.findExhibitionWorks(exhibition);

        // 조회수 증가
        exhibitionDomainService.incrementView(exhibition);

        // 요청한 유저의 전시회 좋아요 북마크 유무
        Long userId = exhibitionDetailsRequest.getUserId();
        boolean isLiked = exhibitionDomainService.isLiked(userId, exhibitionId);
        boolean isBookmarked = exhibitionDomainService.isBookmarked(userId, exhibitionId);

        // 업데이트 마킹
        exhibitionDomainService.markAsUpdated(exhibition);

        return ExhibitionDetailsResponse.of(
                exhibition,
                exhibitionWorks,
                isLiked,
                isBookmarked
        );
    }

    @Override
    @Transactional
    @Cacheable(
            value = "searchExhibitionPage",
            key = "#pageable.pageNumber", // 페이지 번호를 캐싱 키로 지정
            condition = "#pageable.pageNumber <= 10 and #exhibitionSearchRequest.keywords.isEmpty()" // 초반 페이지만 캐싱
    )
    public Page<ExhibitionSearchResponse> searchExhibitions(
            final ExhibitionSearchRequest exhibitionSearchRequest,
            final Pageable pageable
    ) {

        // 검색조건
        Target target = exhibitionSearchRequest.getTarget();
        List<String> keywords = exhibitionSearchRequest.getKeywords();

        Page<ExhibitionSearch> exhibitionSearchPage = exhibitionDomainService.searchExhibitions(
                target, keywords, pageable
        );

        // 전시회에서 유저아이디에 해당하는 좋아요와 북마크 선별해저 담기
        Long userId = exhibitionSearchRequest.getUserId();

        List<ExhibitionSearchResponse> exhibitionSearchResponsePage = exhibitionSearchPage.stream()
                .map(exhibitionSearch -> {
                    long exhibitionId = exhibitionSearch.getId();
                    boolean isLiked = exhibitionDomainService.isLiked(userId, exhibitionId);
                    boolean isBookmarked = exhibitionDomainService.isBookmarked(userId, exhibitionId);

                    return ExhibitionSearchResponse.of(exhibitionSearch, isLiked, isBookmarked);
                })
                .toList();

        return new RestPage<>(exhibitionSearchResponsePage, pageable, exhibitionSearchPage.getTotalElements());
    }

    @Override
    @Transactional
    public void removeExhibition(final Long exhibitionId) {
        // 전시회 조회
        Exhibition exhibition = exhibitionDomainService.findExhibition(exhibitionId);

        // 전시회 개별작품 조회
        List<ExhibitionWork> exhibitionWorks = exhibitionDomainService.findExhibitionWork(exhibition);

        // s3 이미지 & 전시회 개별작품 데이터 삭제
        exhibitionWorks.forEach(exhibitionWork -> {
            String imageUrl = exhibitionWork.getImage();
            imageDomainService.delete(imageUrl);
            exhibitionDomainService.deleteExhibitionWork(exhibitionWork);
        });

        // 좋아요 기록 삭제
        exhibitionDomainService.deleteExhibitionLike(exhibition);

        // 북마크 기록 삭제
        exhibitionDomainService.deleteExhibitionBookmark(exhibition);

        // 관련 태그 삭제
        exhibitionDomainService.deleteExhibitionTag(exhibition);

        // 관련 댓글 삭제
        exhibitionCommentDomainService.deleteComment(exhibition);

        // 전시회 삭제
        exhibitionDomainService.deleteExhibition(exhibition);
    }

    @Override
    @Transactional
    public void incrementLike(final ExhibitionLikeIncrementRequest exhibitionLikeIncrementRequest) {
        // 유저존재확인
        Long userId = exhibitionLikeIncrementRequest.getUserId();
        User user = userDomainService.findUser(userId);

        // 전시회 존재 확인
        Long exhibitionId = exhibitionLikeIncrementRequest.getExhibitionId();
        Exhibition exhibition = exhibitionDomainService.findExhibition(exhibitionId);

        // 이미 좋아요를 했다면 409응답
        exhibitionDomainService.isAlreadyLiked(user, exhibition);

        // 좋아요 데이터 저장
        ExhibitionLike exhibitionLike = exhibitionLikeIncrementRequest.toEntity(user, exhibition);
        exhibitionDomainService.incrementLike(exhibitionLike);

        // 알림생성
        Long exhibitionWriterId = exhibition.getWriter().getId();
        User exhibitionWriter = userDomainService.findUser(exhibitionWriterId);

        Notification notification = Notification.builder()
                .user(exhibitionWriter)
                .type(Type.EXHIBITION_LIKE)
                .targetId(exhibitionId)
                .build();

        notification = notificationDomainService.createNotification(notification);

        // 알림 비동기 처리
        Long notificationId = notification.getId();
        notificationDomainService.pushNewNotification(exhibitionWriterId, notificationId);

        // 업데이트 마킹
        exhibitionDomainService.markAsUpdated(exhibition);
    }

    @Override
    @Transactional
    public void decrementLike(final ExhibitionLikeDecrementRequest exhibitionLikeDecrementRequest) {
        // 유저존재확인
        Long userId = exhibitionLikeDecrementRequest.getUserId();
        User user = userDomainService.findUser(userId);

        // 전시회 존재 확인
        Long exhibitionId = exhibitionLikeDecrementRequest.getExhibitionId();
        Exhibition exhibition = exhibitionDomainService.findExhibition(exhibitionId);

        // 좋아요 삭제
        exhibitionDomainService.decrementLike(user, exhibition);

        // 업데이트 마킹
        exhibitionDomainService.markAsUpdated(exhibition);
    }

    @Override
    @Transactional
    public void addBookmark(final ExhibitionBookmarkRequest exhibitionBookmarkRequest) {
        // 유저존재확인
        Long userId = exhibitionBookmarkRequest.getUserId();
        User user = userDomainService.findUser(userId);

        // 전시회 존재 확인
        Long exhibitionId = exhibitionBookmarkRequest.getExhibitionId();
        Exhibition exhibition = exhibitionDomainService.findExhibition(exhibitionId);

        // 이미 북마크 했다면 409응답
        exhibitionDomainService.isAlreadyBookmarked(user, exhibition);

        // 좋아요 데이터 저장
        ExhibitionBookmark exhibitionBookmark = exhibitionBookmarkRequest.toEntity(user, exhibition);
        exhibitionDomainService.addBookmark(exhibitionBookmark);

        // 알림생성
        Long exhibitionWriterId = exhibition.getWriter().getId();
        User exhibitionWriter = userDomainService.findUser(exhibitionWriterId);

        Notification notification = Notification.builder()
                .user(exhibitionWriter)
                .type(Type.EXHIBITION_BOOKMARK)
                .targetId(exhibitionId)
                .build();

        notification = notificationDomainService.createNotification(notification);

        // 알림 비동기 처리
        Long notificationId = notification.getId();
        notificationDomainService.pushNewNotification(exhibitionWriterId, notificationId);
    }

    @Override
    @Transactional
    public void removeBookmark(final ExhibitionBookmarkRemoveRequest exhibitionBookmarkRemoveRequest) {
        // 유저존재확인
        Long userId = exhibitionBookmarkRemoveRequest.getUserId();
        User user = userDomainService.findUser(userId);

        // 전시회 존재 확인
        Long exhibitionId = exhibitionBookmarkRemoveRequest.getExhibitionId();
        Exhibition exhibition = exhibitionDomainService.findExhibition(exhibitionId);

        // 북마크 삭제
        exhibitionDomainService.removeBookmark(user, exhibition);
    }

    @Override
    @Transactional
    public Page<BookmarkedExhibitionResponse> getBookmarkedExhibitions(
            final BookmarkedExhibitionRequest bookmarkedExhibitionRequest,
            final Pageable pageable
    ) {
        // 요청 유저 조회
        Long userId = bookmarkedExhibitionRequest.getUserId();

        // 유저가 북마크한 전시회조회
        Page<ExhibitionSearch> exhibitionSearchPage = exhibitionDomainService.findBookmarkedExhibitionsByUser(userId,
                pageable);

        // 전시회에서 유저아이디에 해당하는 좋아요와 북마크 선별해서 담기
        List<BookmarkedExhibitionResponse> bookmarkedExhibitionResponsePage = exhibitionSearchPage.stream()
                .map(exhibitionSearch -> {
                    long exhibitionId = exhibitionSearch.getId();
                    boolean isLiked = exhibitionDomainService.isLiked(userId, exhibitionId);

                    return BookmarkedExhibitionResponse.of(exhibitionSearch, isLiked);
                })
                .toList();

        return new PageImpl<>(bookmarkedExhibitionResponsePage, pageable, exhibitionSearchPage.getTotalElements());
    }

    @Override
    @Transactional
    public Page<LikedExhibitionResponse> getLikedExhibitions(
            final LikedExhibitionRequest likedExhibitionRequest,
            final Pageable pageable
    ) {
        // 요청 유저 조회
        Long userId = likedExhibitionRequest.getUserId();

        // 유저가 좋아요한 전시회 페이지 조회
        Page<ExhibitionSearch> exhibitionSearchPage = exhibitionDomainService.findLikedExhibitionsByUser(userId,
                pageable);

        // 북마크 데이터 조회
        List<LikedExhibitionResponse> likedExhibitionResponsePage = exhibitionSearchPage.stream()
                .map(exhibitionSearch -> {
                    long exhibitionId = exhibitionSearch.getId();
                    boolean isBookmarked = exhibitionDomainService.isBookmarked(userId, exhibitionId);

                    return LikedExhibitionResponse.of(exhibitionSearch, isBookmarked);
                })
                .toList();

        return new PageImpl<>(likedExhibitionResponsePage, pageable, exhibitionSearchPage.getTotalElements());
    }

    @Override
    @Transactional
    public Page<MyExhibitionResponse> getMyExhibitions(
            final MyExhibitionRequest myExhibitionRequest,
            final Pageable pageable
    ) {
        Long userId = myExhibitionRequest.getUserId();

        // 요청 유저의 전시회 페이지 조회
        Page<ExhibitionSearch> exhibitionSearchPage = exhibitionDomainService.findMyExhibitions(userId, pageable);

        // 요청 유저의 좋아요, 북마크 데이터 조회
        // 응답 페이지 생성 및 반환
        List<MyExhibitionResponse> myExhibitionResponsePage = exhibitionSearchPage.stream()
                .map(exhibitionSearch -> {
                    long exhibitionId = exhibitionSearch.getId();
                    boolean isLiked = exhibitionDomainService.isLiked(userId, exhibitionId);
                    boolean isBookmarked = exhibitionDomainService.isBookmarked(userId, exhibitionId);

                    return MyExhibitionResponse.of(exhibitionSearch, isLiked, isBookmarked);
                })
                .toList();

        return new PageImpl<>(myExhibitionResponsePage, pageable, exhibitionSearchPage.getTotalElements());
    }
}
