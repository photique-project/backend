package com.benchpress200.photique.singlework.application;


import com.benchpress200.photique.image.domain.ImageDomainService;
import com.benchpress200.photique.singlework.domain.SingleWorkCommentDomainService;
import com.benchpress200.photique.singlework.domain.SingleWorkDomainService;
import com.benchpress200.photique.singlework.domain.dto.SingleWorkCreateRequest;
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
import com.benchpress200.photique.singlework.domain.enumeration.Category;
import com.benchpress200.photique.singlework.domain.enumeration.Target;
import com.benchpress200.photique.tag.domain.TagDomainService;
import com.benchpress200.photique.tag.domain.entity.Tag;
import com.benchpress200.photique.user.domain.UserDomainService;
import com.benchpress200.photique.user.domain.entity.User;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
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
public class SingleWorkServiceImpl implements SingleWorkService {
    @Value("${cloud.aws.s3.path.single-work}")
    private String imagePath;

    private final ImageDomainService imageDomainService;
    private final UserDomainService userDomainService;
    private final SingleWorkDomainService singleWorkDomainService;
    private final SingleWorkCommentDomainService singleWorkCommentDomainService;
    private final TagDomainService tagDomainService;

    
    @Override
    @Transactional
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
    }


    @Override
    @Transactional
    public SingleWorkDetailResponse getSingleWorkDetail(final Long singleWorkId) {
        // 단일작품 조회
        SingleWork singleWork = singleWorkDomainService.findSingleWork(singleWorkId);

        // 조회수 증가
        singleWorkDomainService.incrementView(singleWork);

        // 단일작품 태그 리스트 조회
        List<SingleWorkTag> singleWorkTags = singleWorkDomainService.findSingleWorkTag(singleWork);

        // 단일작품 태그와 매칭되는 태그 조회
        List<Tag> tags = tagDomainService.findTags(singleWorkTags);

        // 단일작품 좋아요 수 조회
        Long likeCount = singleWorkDomainService.countLike(singleWork);

        return SingleWorkDetailResponse.from(
                singleWork,
                tags,
                likeCount
        );
    }

    @Override
    public Page<SingleWorkSearchResponse> searchSingleWorks(
            final SingleWorkSearchRequest singleWorkSearchRequest,
            final Pageable pageable
    ) {

        // 테스트를 위해 사용했던 RDBMS 검색로직
//        Page<SingleWork> singleWorks = singleWorkRepository.searchSingleWorks(
//                singleWorkSearchRequest.getTarget(),
//                singleWorkSearchRequest.getKeywords(),
//                singleWorkSearchRequest.getCategories(),
//                pageable
//        );

        // 검색조건
        Target target = singleWorkSearchRequest.getTarget();
        List<String> keywords = singleWorkSearchRequest.getKeywords();
        List<Category> categories = singleWorkSearchRequest.getCategories();

        // 단일작품 검색
        Page<SingleWorkSearch> singleWorkSearchPage = singleWorkDomainService.searchSingleWorks(target, keywords,
                categories, pageable);

        // 응답 dto 로 변환
        List<SingleWorkSearchResponse> singleWorkSearchResponsePage = singleWorkSearchPage.stream()
                .map(SingleWorkSearchResponse::from)
                .toList();

        return new PageImpl<>(singleWorkSearchResponsePage, pageable, singleWorkSearchPage.getTotalElements());
    }

    @Override
    @Transactional
    public void updateSingleWorkDetail(final SingleWorkUpdateRequest singleWorkUpdateRequest) {
        // 단일작품 조회
        Long singleWorkId = singleWorkUpdateRequest.getId();
        SingleWork singleWork = singleWorkDomainService.findSingleWork(singleWorkId);

        // 이미지 업데이트
        String oldImageUrl = singleWork.getImage();
        MultipartFile newImage = singleWorkUpdateRequest.getImage();
        String updatedNewImageUrl = imageDomainService.update(newImage, oldImageUrl, imagePath); // 이미지 업데이트 로직확인
        singleWorkDomainService.updateImage(singleWork, updatedNewImageUrl);

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
    }

    @Override
    @Transactional
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
        singleWorkDomainService.isLiked(user, singleWork);

        // 작품 좋아요 추가
        SingleWorkLike singleWorkLike = singleWorkLikeIncrementRequest.toEntity(user, singleWork);
        singleWorkDomainService.incrementLike(singleWorkLike);
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
    }
}
