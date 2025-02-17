package com.benchpress200.photique.singlework.application;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.UpdateRequest;
import com.benchpress200.photique.common.domain.dto.NewTagRequest;
import com.benchpress200.photique.common.domain.entity.Tag;
import com.benchpress200.photique.common.infrastructure.TagRepository;
import com.benchpress200.photique.image.infrastructure.ImageUploader;
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
import com.benchpress200.photique.singlework.domain.enumeration.Aperture;
import com.benchpress200.photique.singlework.domain.enumeration.Category;
import com.benchpress200.photique.singlework.domain.enumeration.ISO;
import com.benchpress200.photique.singlework.domain.enumeration.ShutterSpeed;
import com.benchpress200.photique.singlework.exception.SingleWorkException;
import com.benchpress200.photique.singlework.infrastructure.SingleWorkCommentRepository;
import com.benchpress200.photique.singlework.infrastructure.SingleWorkLikeRepository;
import com.benchpress200.photique.singlework.infrastructure.SingleWorkRepository;
import com.benchpress200.photique.singlework.infrastructure.SingleWorkSearchRepository;
import com.benchpress200.photique.singlework.infrastructure.SingleWorkTagRepository;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.infrastructure.UserRepository;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class SingleWorkServiceImpl implements SingleWorkService {
    private final SingleWorkCommentRepository singleWorkCommentRepository;
    @Value("${cloud.aws.s3.path.single-work}")
    private String imagePath;


    private final ImageUploader imageUploader;
    private final UserRepository userRepository;
    private final SingleWorkRepository singleWorkRepository;
    private final SingleWorkTagRepository singleWorkTagRepository;
    private final TagRepository tagRepository;
    private final SingleWorkSearchRepository singleWorkSearchRepository;
    private final SingleWorkLikeRepository singleWorkLikeRepository;
    private final ElasticsearchClient elasticsearchClient;


    @Override
    public void createNewSingleWork(final SingleWorkCreateRequest singleWorkCreateRequest) {

        // 작성자 조회
        final Long writerId = singleWorkCreateRequest.getWriterId();
        final User writer = userRepository.findById(writerId).orElseThrow(
                () -> new SingleWorkException("User with ID " + writerId + " is not found.", HttpStatus.NOT_FOUND)
        );

        // 이미지 업로드 & 단일작품 저장
        final String imageUrl = imageUploader.upload(singleWorkCreateRequest.getImage(), imagePath);
        final SingleWork savedSingleWork = singleWorkRepository.save(
                singleWorkCreateRequest.toSingleWorkEntity(writer, imageUrl));

        // 태그 데이터 유무 확인
        List<String> tagNames = null;
        if (singleWorkCreateRequest.hasTags()) {
            List<NewTagRequest> tags = singleWorkCreateRequest.getTags();

            // 태그이름 리스트 생성
            tagNames = tags.stream()
                    .map(NewTagRequest::getName)
                    .collect(Collectors.toList());

            // 데이터베이스에 존재하는 태그이름 리스트 생성
            List<String> existingTagNames = tagRepository.findAllByNameIn(tagNames)
                    .stream()
                    .map(Tag::getName)
                    .toList();

            // 존재하지 않는 태그 리스트 생성
            List<Tag> newTags = tags.stream()
                    .filter(tagRequest -> !existingTagNames.contains(tagRequest.getName()))
                    .map(NewTagRequest::toEntity)
                    .toList();

            // 새로운 태그 저장
            if (!newTags.isEmpty()) {
                tagRepository.saveAll(newTags);
            }

            // 요청한 모든 태그 엔티티 조회
            List<Tag> allTags = tagRepository.findAllByNameIn(tagNames);

            // SingleWorkTag 엔티티 생성
            List<SingleWorkTag> singleWorkTags = singleWorkCreateRequest.toSingleWorkTagEntities(savedSingleWork,
                    allTags);

            // SingleWorkTag 엔티티 저장
            singleWorkTagRepository.saveAll(singleWorkTags);
        }

        // 엘라스틱 서치 저장
        SingleWorkSearch singleWorkSearch = SingleWorkSearch.builder()
                .id(savedSingleWork.getId())
                .image(savedSingleWork.getImage())
                .writerId(writer.getId())
                .writerNickname(writer.getNickname())
                .writerProfileImage(writer.getProfileImage())
                .title(savedSingleWork.getTitle())
                .tags(tagNames)
                .category(savedSingleWork.getCategory().getValue())
                .likeCount(savedSingleWork.getLikeCount())
                .viewCount(savedSingleWork.getViewCount())
                .commentCount(0L)
                .createdAt(savedSingleWork.getCreatedAt())
                .build();

        singleWorkSearchRepository.save(singleWorkSearch);
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

        Page<SingleWorkSearch> singleWorkSearchPage = singleWorkSearchRepository.searchSingleWorks(
                singleWorkSearchRequest.getTarget(),
                singleWorkSearchRequest.getKeywords(),
                singleWorkSearchRequest.getCategories(),
                pageable
        );

        List<SingleWorkSearchResponse> singleWorkSearchResponsePage = singleWorkSearchPage.stream()
                .map(SingleWorkSearchResponse::from)
                .toList();

        return new PageImpl<>(singleWorkSearchResponsePage, pageable, singleWorkSearchPage.getTotalElements());
    }


    @Override
    public SingleWorkDetailResponse getSingleWorkDetail(final Long singleWorkId) {
        // elastic search 데이터 업데이트를 위한 컬렉션
        Map<String, Object> updateFields = new HashMap<>();

        SingleWork singleWork = singleWorkRepository.findById(singleWorkId).orElseThrow(
                () -> new SingleWorkException("SingleWork with ID " + singleWorkId + " is not found.",
                        HttpStatus.NOT_FOUND)
        );

        singleWork.incrementView();
        updateFields.put("viewCount", singleWork.getViewCount());

        UpdateRequest<Map<String, Object>, ?> updateRequest = UpdateRequest.of(u -> u
                .index("singleworks")
                .id(singleWork.getId().toString())
                .doc(updateFields)
        );

        try {
            elasticsearchClient.update(updateRequest, Map.class);
        } catch (IOException e) {
            throw new SingleWorkException("Elastic search network error", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        List<SingleWorkTag> singleWorkTags = singleWorkTagRepository.findBySingleWorkId(singleWorkId);

        List<Tag> tags = singleWorkTags.stream()
                .map(SingleWorkTag::getTag)
                .toList();

        return SingleWorkDetailResponse.from(
                singleWork,
                tags
        );
    }

    @Override
    public void updateSingleWorkDetail(final SingleWorkUpdateRequest singleWorkUpdateRequest) {
        Long singleWorkId = singleWorkUpdateRequest.getId();
        SingleWork singleWork = singleWorkRepository.findById(singleWorkId).orElseThrow(
                () -> new SingleWorkException("SingleWork with ID " + singleWorkId + " is not found.",
                        HttpStatus.NOT_FOUND)
        );

        // elastic search 데이터 업데이트를 위한 컬렉션
        Map<String, Object> updateFields = new HashMap<>();

        // 업데이트 이미지가 있다면
        if (singleWorkUpdateRequest.hasImage()) {
            if (singleWorkUpdateRequest.isEmptyImage()) { // 빈 이미지로 업데이트 불가능
                throw new SingleWorkException("Invalid Image", HttpStatus.BAD_REQUEST);
            }

            String image = singleWork.getImage();
            image = imageUploader.update(
                    singleWorkUpdateRequest.getImage(),
                    image,
                    imagePath
            );

            singleWork.updateImage(image);
            updateFields.put("image", image);
        }

        // 업데이트 카메라가 있다면
        if (singleWorkUpdateRequest.hasCamera()) {
            if (singleWorkUpdateRequest.isEmptyCamera()) { // 빈 카메라 업데이트 불가능
                throw new SingleWorkException("Invalid Camera", HttpStatus.BAD_REQUEST);
            }

            String camera = singleWorkUpdateRequest.getCamera();
            singleWork.updateCamera(camera);
            updateFields.put("camera", camera);
        }

        // 업데이트 렌즈가 있다면
        if (singleWorkUpdateRequest.hasLens()) {
            String lens = getLens(singleWorkUpdateRequest); // null이 아닌 빈 값이라면 기본값으로 null 세팅
            singleWork.updateLens(lens);
            updateFields.put("lens", lens);
        }

        // 업데이트 조리개 값이 있다면
        if (singleWorkUpdateRequest.hasAperture()) {
            Aperture aperture = getAperture(singleWorkUpdateRequest);
            singleWork.updateAperture(aperture);
            updateFields.put("aperture", aperture);
        }

        // 업데이트 셔터스피드 값이 있다면
        if (singleWorkUpdateRequest.hasShutterSpeed()) {
            ShutterSpeed shutterSpeed = getShutterSpeed(singleWorkUpdateRequest);
            singleWork.updateShutterSpeed(shutterSpeed);
            updateFields.put("shutterSpeed", shutterSpeed);
        }

        // 업데이트 ISO 값이 있다면
        if (singleWorkUpdateRequest.hasIso()) {
            ISO iso = getIso(singleWorkUpdateRequest);
            singleWork.updateIso(iso);
            updateFields.put("iso", iso);
        }

        // 업데이트 위치가 있다면
        if (singleWorkUpdateRequest.hasLocation()) {
            String location = getLocation(singleWorkUpdateRequest);
            singleWork.updateLocation(location);
            updateFields.put("location", location);
        }

        // 업데이트 카테고리가 있다면
        if (singleWorkUpdateRequest.hasCategory()) {
            if (singleWorkUpdateRequest.isEmptyCategory()) {
                throw new SingleWorkException("Invalid Category", HttpStatus.BAD_REQUEST);
            }

            String category = singleWorkUpdateRequest.getCategory();
            singleWork.updateCategory(Category.fromValue(category));
            updateFields.put("category", category);
        }

        // 업데이트 날짜가 있다면
        if (singleWorkUpdateRequest.hasDate()) {
            LocalDate date = singleWorkUpdateRequest.getDate();
            singleWork.updateDate(date);
            updateFields.put("date", date);
        }

        // 업데이트 태그가 있다면
        if (singleWorkUpdateRequest.hasTags()) {
            // 기존 태그 모두 삭제하고
            singleWorkTagRepository.deleteBySingleWorkId(singleWorkId);

            List<NewTagRequest> tags = singleWorkUpdateRequest.getTags();

            // 태그이름 리스트 생성
            List<String> tagNames = tags.stream()
                    .map(NewTagRequest::getName)
                    .collect(Collectors.toList());

            // 데이터베이스에 존재하는 태그이름 리스트 생성
            List<String> existingTagNames = tagRepository.findAllByNameIn(tagNames)
                    .stream()
                    .map(Tag::getName)
                    .toList();

            // 존재하지 않는 태그 리스트 생성
            List<Tag> newTags = tags.stream()
                    .filter(tagRequest -> !existingTagNames.contains(tagRequest.getName()))
                    .map(NewTagRequest::toEntity)
                    .toList();

            // 새로운 태그 저장
            if (!newTags.isEmpty()) {
                tagRepository.saveAll(newTags);
            }

            // 요청한 모든 태그 엔티티 조회
            List<Tag> allTags = tagRepository.findAllByNameIn(tagNames);

            // SingleWorkTag 엔티티 생성
            List<SingleWorkTag> singleWorkTags = allTags.stream()
                    .map(tag -> SingleWorkTag.builder()
                            .singleWork(singleWork)
                            .tag(tag)
                            .build())
                    .toList();

            // SingleWorkTag 엔티티 저장
            singleWorkTagRepository.saveAll(singleWorkTags);
            updateFields.put("tags", tagNames);
        }

        // 업데이트 타이틀이 있다면
        if (singleWorkUpdateRequest.hasTitle()) {
            if (singleWorkUpdateRequest.isEmptyTitle()) {
                throw new SingleWorkException("Invalid Title", HttpStatus.BAD_REQUEST);
            }

            String title = singleWorkUpdateRequest.getTitle();
            singleWork.updateTitle(title);
            updateFields.put("title", title);
        }

        // 업데이트 설명이 있다면
        if (singleWorkUpdateRequest.hasDescription()) {
            if (singleWorkUpdateRequest.isEmptyDescription()) {
                throw new SingleWorkException("Invalid Description", HttpStatus.BAD_REQUEST);
            }

            String description = singleWorkUpdateRequest.getDescription();
            singleWork.updateDescription(description);
            updateFields.put("description", description);
        }

        UpdateRequest<Map<String, Object>, ?> updateRequest = UpdateRequest.of(u -> u
                .index("singleworks")
                .id(singleWork.getId().toString())
                .doc(updateFields)
        );

        try {
            elasticsearchClient.update(updateRequest, Map.class);
        } catch (IOException e) {
            throw new SingleWorkException("Elastic search network error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void removeSingleWork(final Long singleworkId) {
        SingleWork singleWork = singleWorkRepository.findById(singleworkId).orElseThrow(
                () -> new SingleWorkException("SingleWork with id " + singleworkId + " not found", HttpStatus.NOT_FOUND)
        );

        // s3 이미지 삭제
        String imageUrl = singleWork.getImage();
        if (imageUrl != null) {
            imageUploader.delete(imageUrl);
        }

        // 관련 댓글 삭제
        singleWorkCommentRepository.deleteBySingleWorkId(singleworkId);

        // 관련 태그 삭제
        singleWorkTagRepository.deleteBySingleWorkId(singleworkId);

        // 좋아요 기록 삭제
        singleWorkLikeRepository.deleteBySingleWorkId(singleworkId);

        // 작품 삭제
        singleWorkRepository.deleteById(singleworkId);

        // 엘라스틱 서치 삭제
        singleWorkSearchRepository.deleteById(singleworkId);
    }

    @Override
    public void incrementLike(final SingleWorkLikeIncrementRequest singleWorkLikeIncrementRequest) {
        // elastic search 데이터 업데이트를 위한 컬렉션
        Map<String, Object> updateFields = new HashMap<>();

        // 유저존재확인
        Long userId = singleWorkLikeIncrementRequest.getUserId();
        User user = userRepository.findById(userId).orElseThrow(
                () -> new SingleWorkException("User with id " + userId + " not found", HttpStatus.NOT_FOUND)
        );

        // 작품존재확인
        Long singleWorkId = singleWorkLikeIncrementRequest.getSingleWorkId();
        SingleWork singleWork = singleWorkRepository.findById(singleWorkId).orElseThrow(
                () -> new SingleWorkException("SingleWork with id " + singleWorkId + " not found", HttpStatus.NOT_FOUND)
        );

        // 작품 좋아요 추가
        SingleWorkLike singleWorkLike = singleWorkLikeIncrementRequest.toEntity(user, singleWork);
        singleWorkLikeRepository.save(singleWorkLike);
        singleWork.incrementLike();

        updateFields.put("likeCount", singleWork.getLikeCount());

        UpdateRequest<Map<String, Object>, ?> updateRequest = UpdateRequest.of(u -> u
                .index("singleworks")
                .id(singleWork.getId().toString())
                .doc(updateFields)
        );

        try {
            elasticsearchClient.update(updateRequest, Map.class);
        } catch (IOException e) {
            throw new SingleWorkException("Elastic search network error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void decrementLike(final SingleWorkLikeDecrementRequest singleWorkLikeDecrementRequest) {
        // elastic search 데이터 업데이트를 위한 컬렉션
        Map<String, Object> updateFields = new HashMap<>();

        // 유저존재확인
        Long userId = singleWorkLikeDecrementRequest.getUserId();
        userRepository.findById(userId).orElseThrow(
                () -> new SingleWorkException("User with id " + userId + " not found", HttpStatus.NOT_FOUND)
        );

        // 작품존재확인
        Long singleWorkId = singleWorkLikeDecrementRequest.getSingleWorkId();
        SingleWork singleWork = singleWorkRepository.findById(singleWorkId).orElseThrow(
                () -> new SingleWorkException("SingleWork with id " + singleWorkId + " not found", HttpStatus.NOT_FOUND)
        );

        singleWorkLikeRepository.deleteByUserIdAndSingleWorkId(userId, singleWorkId);
        singleWork.decrementLike();

        updateFields.put("likeCount", singleWork.getLikeCount());

        UpdateRequest<Map<String, Object>, ?> updateRequest = UpdateRequest.of(u -> u
                .index("singleworks")
                .id(singleWork.getId().toString())
                .doc(updateFields)
        );

        try {
            elasticsearchClient.update(updateRequest, Map.class);
        } catch (IOException e) {
            throw new SingleWorkException("Elastic search network error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String getLocation(SingleWorkUpdateRequest singleWorkUpdateRequest) {
        if (singleWorkUpdateRequest.isEmptyLocation()) {
            return null;
        }

        return singleWorkUpdateRequest.getLocation();
    }

    private ISO getIso(SingleWorkUpdateRequest singleWorkUpdateRequest) {
        if (singleWorkUpdateRequest.isEmptyIso()) {
            return null;
        }

        return ISO.fromValue(singleWorkUpdateRequest.getShutterSpeed());
    }

    private ShutterSpeed getShutterSpeed(SingleWorkUpdateRequest singleWorkUpdateRequest) {
        if (singleWorkUpdateRequest.isEmptyShutterSpeed()) {
            return null;
        }

        return ShutterSpeed.fromValue(singleWorkUpdateRequest.getShutterSpeed());
    }

    private Aperture getAperture(SingleWorkUpdateRequest singleWorkUpdateRequest) {
        if (singleWorkUpdateRequest.isEmptyAperture()) {
            return null;
        }

        return Aperture.fromValue(singleWorkUpdateRequest.getAperture());
    }

    private String getLens(SingleWorkUpdateRequest singleWorkUpdateRequest) {
        if (singleWorkUpdateRequest.isEmptyLens()) {
            return null;
        }

        return singleWorkUpdateRequest.getLens();
    }

}
