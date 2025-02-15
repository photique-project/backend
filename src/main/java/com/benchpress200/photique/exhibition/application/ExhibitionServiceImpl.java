package com.benchpress200.photique.exhibition.application;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.UpdateRequest;
import com.benchpress200.photique.common.domain.dto.NewTagRequest;
import com.benchpress200.photique.common.domain.entity.Tag;
import com.benchpress200.photique.common.infrastructure.ImageUploader;
import com.benchpress200.photique.common.infrastructure.TagRepository;
import com.benchpress200.photique.exhibition.domain.dto.ExhibitionBookmarkRemoveRequest;
import com.benchpress200.photique.exhibition.domain.dto.ExhibitionBookmarkRequest;
import com.benchpress200.photique.exhibition.domain.dto.ExhibitionCreateRequest;
import com.benchpress200.photique.exhibition.domain.dto.ExhibitionDetailResponse;
import com.benchpress200.photique.exhibition.domain.dto.ExhibitionLikeDecrementRequest;
import com.benchpress200.photique.exhibition.domain.dto.ExhibitionLikeIncrementRequest;
import com.benchpress200.photique.exhibition.domain.dto.ExhibitionSearchRequest;
import com.benchpress200.photique.exhibition.domain.dto.ExhibitionSearchResponse;
import com.benchpress200.photique.exhibition.domain.dto.ExhibitionWorkCreateRequest;
import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionBookmark;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionLike;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionSearch;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionTag;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionWork;
import com.benchpress200.photique.exhibition.exception.ExhibitionException;
import com.benchpress200.photique.exhibition.infrastructure.ExhibitionBookmarkRepository;
import com.benchpress200.photique.exhibition.infrastructure.ExhibitionCommentRepository;
import com.benchpress200.photique.exhibition.infrastructure.ExhibitionLikeRepository;
import com.benchpress200.photique.exhibition.infrastructure.ExhibitionRepository;
import com.benchpress200.photique.exhibition.infrastructure.ExhibitionSearchRepository;
import com.benchpress200.photique.exhibition.infrastructure.ExhibitionTagRepository;
import com.benchpress200.photique.exhibition.infrastructure.ExhibitionWorkRepository;
import com.benchpress200.photique.singlework.exception.SingleWorkException;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.infrastructure.UserRepository;
import jakarta.transaction.Transactional;
import java.io.IOException;
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
public class ExhibitionServiceImpl implements ExhibitionService {
    @Value("${cloud.aws.s3.path.exhibition}")
    private String imagePath;

    private final ImageUploader imageUploader;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final ExhibitionRepository exhibitionRepository;
    private final ExhibitionTagRepository exhibitionTagRepository;
    private final ExhibitionWorkRepository exhibitionWorkRepository;
    private final ExhibitionSearchRepository exhibitionSearchRepository;
    private final ExhibitionCommentRepository exhibitionCommentRepository;
    private final ExhibitionLikeRepository exhibitionLikeRepository;
    private final ExhibitionBookmarkRepository exhibitionBookmarkRepository;
    private final ElasticsearchClient elasticsearchClient;

    @Override
    public void createNewExhibition(final ExhibitionCreateRequest exhibitionCreateRequest) {
        // 작가 조회
        User writer = userRepository.findById(exhibitionCreateRequest.getWriterId()).orElseThrow(
                () -> new ExhibitionException("", HttpStatus.NOT_FOUND)
        );

        // 워크 리스트 순회하면서 유저 조회 하고 이미지 변환하고 전시회 작품 엔티티 저장
        Exhibition savedExhibition = exhibitionRepository.save(exhibitionCreateRequest.toEntity(writer));

        List<ExhibitionWorkCreateRequest> works = exhibitionCreateRequest.getWorks();
        List<ExhibitionWork> exhibitionWorks = works.stream()
                .map(work -> {
                    User user = userRepository.findById(work.getWriterId())
                            .orElseThrow(() -> new ExhibitionException("User with ID '12345' is not found.",
                                    HttpStatus.NOT_FOUND));
                    String imageUrl = imageUploader.upload(work.getImage(), imagePath);

                    return work.toEntity(savedExhibition, user, imageUrl);
                })
                .toList();
        exhibitionWorkRepository.saveAll(exhibitionWorks);

        // 새 태그틑 저장
        // 전시회 태그 엔티티 저장
        List<String> tagNames = null;
        if (exhibitionCreateRequest.hasTags()) {
            List<NewTagRequest> tags = exhibitionCreateRequest.getTags();

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

            // ExhibitionTag 엔티티 생성
            List<ExhibitionTag> exhibitionTags = exhibitionCreateRequest.toExhibitionTagEntities(savedExhibition,
                    allTags);

            // SingleWorkTag 엔티티 저장
            exhibitionTagRepository.saveAll(exhibitionTags);
        }

        // 엘라스틱 서치 저장
        ExhibitionSearch exhibitionSearch = ExhibitionSearch.builder()
                .id(savedExhibition.getId())
                .writerId(writer.getId())
                .writerNickname(writer.getNickname())
                .writerProfileImage(writer.getProfileImage())
                .introduction(writer.getIntroduction())
                .participants(exhibitionWorks.size())
                .cardColor(savedExhibition.getCardColor())
                .title(savedExhibition.getTitle())
                .description(savedExhibition.getDescription())
                .tags(tagNames)
                .likeCount(0L)
                .viewCount(0L)
                .commentCount(0L)
                .createdAt(savedExhibition.getCreatedAt())
                .build();

        exhibitionSearchRepository.save(exhibitionSearch);
    }

    @Override
    public ExhibitionDetailResponse getExhibitionDetail(final Long exhibitionId) {
        // elastic search 데이터 업데이트를 위한 컬렉션
        Map<String, Object> updateFields = new HashMap<>();

        Exhibition exhibition = exhibitionRepository.findById(exhibitionId).orElseThrow(
                () -> new ExhibitionException("Exhibition with ID " + exhibitionId + " is not found.",
                        HttpStatus.NOT_FOUND)
        );

        List<ExhibitionWork> exhibitionWorks = exhibitionWorkRepository.findByExhibitionId(exhibitionId);

        exhibition.incrementView();
        updateFields.put("viewCount", exhibition.getViewCount());

        UpdateRequest<Map<String, Object>, ?> updateRequest = UpdateRequest.of(u -> u
                .index("exhibitions")
                .id(exhibition.getId().toString())
                .doc(updateFields)
        );

        try {
            elasticsearchClient.update(updateRequest, Map.class);
        } catch (IOException e) {
            throw new SingleWorkException("Elastic search network error", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ExhibitionDetailResponse.from(
                exhibition,
                exhibitionWorks
        );
    }

    @Override
    public Page<ExhibitionSearchResponse> searchExhibitions(
            final ExhibitionSearchRequest exhibitionSearchRequest,
            final Pageable pageable
    ) {
        Page<ExhibitionSearch> exhibitionSearchPage = exhibitionSearchRepository.searchExhibitions(
                exhibitionSearchRequest,
                pageable
        );

        List<ExhibitionSearchResponse> exhibitionSearchResponsePage = exhibitionSearchPage.stream()
                .map(ExhibitionSearchResponse::from)
                .toList();

        return new PageImpl<>(exhibitionSearchResponsePage, pageable, exhibitionSearchPage.getTotalElements());
    }

    @Override
    public void removeExhibition(final Long exhibitionId) {
        List<ExhibitionWork> exhibitionWorks = exhibitionWorkRepository.findByExhibitionId(exhibitionId);

        // s3이미지 & 전시회 관련 이미지 데이터 삭제
        exhibitionWorks.forEach(exhibitionWork -> {
            imageUploader.delete(exhibitionWork.getImage());
            exhibitionWorkRepository.delete(exhibitionWork);
        });

        // 관련 댓글 삭제
        exhibitionCommentRepository.deleteByExhibitionId(exhibitionId);

        // 관련 태그 삭제
        exhibitionTagRepository.deleteByExhibitionId(exhibitionId);

        // 좋아요 기록 삭제
        exhibitionLikeRepository.deleteByExhibitionId(exhibitionId);

        // 북마크 기록 삭제
        exhibitionBookmarkRepository.deleteByExhibitionId(exhibitionId);

        // 전시회 삭제
        exhibitionRepository.deleteById(exhibitionId);

        // 알라스틱 서치 삭제
        exhibitionSearchRepository.deleteById(exhibitionId);
    }

    @Override
    public void incrementLike(final ExhibitionLikeIncrementRequest exhibitionLikeIncrementRequest) {
        // elastic search 데이터 업데이트를 위한 컬렉션
        Map<String, Object> updateFields = new HashMap<>();

        // 유저존재확인
        Long userId = exhibitionLikeIncrementRequest.getUserId();
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ExhibitionException("User with id " + userId + " not found", HttpStatus.NOT_FOUND)
        );

        // 전시회 존재 확인
        Long exhibitionId = exhibitionLikeIncrementRequest.getExhibitionId();
        Exhibition exhibition = exhibitionRepository.findById(exhibitionId).orElseThrow(
                () -> new ExhibitionException("Exhibition with id " + exhibitionId + " not found", HttpStatus.NOT_FOUND)
        );

        // 이미 좋아요를 했다면 409응답
        if (exhibitionLikeRepository.existsByUserAndExhibition(user, exhibition)) {
            throw new ExhibitionException("User has already liked this exhibition", HttpStatus.CONFLICT);
        }

        // 좋아요 데이터 저장
        ExhibitionLike exhibitionLike = exhibitionLikeIncrementRequest.toEntity(user, exhibition);
        exhibitionLikeRepository.save(exhibitionLike);

        exhibition.incrementLike();

        updateFields.put("likeCount", exhibition.getLikeCount());

        UpdateRequest<Map<String, Object>, ?> updateRequest = UpdateRequest.of(u -> u
                .index("exhibitions")
                .id(exhibition.getId().toString())
                .doc(updateFields)
        );

        try {
            elasticsearchClient.update(updateRequest, Map.class);
        } catch (IOException e) {
            throw new SingleWorkException("Elastic search network error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void decrementLike(final ExhibitionLikeDecrementRequest exhibitionLikeDecrementRequest) {
        // elastic search 데이터 업데이트를 위한 컬렉션
        Map<String, Object> updateFields = new HashMap<>();

        // 유저존재확인
        Long userId = exhibitionLikeDecrementRequest.getUserId();
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ExhibitionException("User with id " + userId + " not found", HttpStatus.NOT_FOUND)
        );

        // 전시회 존재 확인
        Long exhibitionId = exhibitionLikeDecrementRequest.getExhibitionId();
        Exhibition exhibition = exhibitionRepository.findById(exhibitionId).orElseThrow(
                () -> new ExhibitionException("Exhibition with id " + exhibitionId + " not found", HttpStatus.NOT_FOUND)
        );

        exhibitionLikeRepository.deleteByUserAndExhibition(user, exhibition);
        exhibition.decrementLike();

        updateFields.put("likeCount", exhibition.getLikeCount());

        UpdateRequest<Map<String, Object>, ?> updateRequest = UpdateRequest.of(u -> u
                .index("exhibitions")
                .id(exhibition.getId().toString())
                .doc(updateFields)
        );

        try {
            elasticsearchClient.update(updateRequest, Map.class);
        } catch (IOException e) {
            throw new SingleWorkException("Elastic search network error", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @Override
    public void addBookmark(final ExhibitionBookmarkRequest exhibitionBookmarkRequest) {
        // 유저존재확인
        Long userId = exhibitionBookmarkRequest.getUserId();
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ExhibitionException("User with id " + userId + " not found", HttpStatus.NOT_FOUND)
        );

        // 전시회 존재 확인
        Long exhibitionId = exhibitionBookmarkRequest.getExhibitionId();
        Exhibition exhibition = exhibitionRepository.findById(exhibitionId).orElseThrow(
                () -> new ExhibitionException("Exhibition with id " + exhibitionId + " not found", HttpStatus.NOT_FOUND)
        );

        // 이미 북마크 했다면 409응답
        if (exhibitionBookmarkRepository.existsByUserAndExhibition(user, exhibition)) {
            throw new ExhibitionException("User has already marked this exhibition", HttpStatus.CONFLICT);
        }

        // 좋아요 데이터 저장
        ExhibitionBookmark exhibitionBookmark = exhibitionBookmarkRequest.toEntity(user, exhibition);
        exhibitionBookmarkRepository.save(exhibitionBookmark);
    }

    @Override
    public void removeBookmark(final ExhibitionBookmarkRemoveRequest exhibitionBookmarkRemoveRequest) {
        // 유저존재확인
        Long userId = exhibitionBookmarkRemoveRequest.getUserId();
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ExhibitionException("User with id " + userId + " not found", HttpStatus.NOT_FOUND)
        );

        // 전시회 존재 확인
        Long exhibitionId = exhibitionBookmarkRemoveRequest.getExhibitionId();
        Exhibition exhibition = exhibitionRepository.findById(exhibitionId).orElseThrow(
                () -> new ExhibitionException("Exhibition with id " + exhibitionId + " not found", HttpStatus.NOT_FOUND)
        );

        exhibitionBookmarkRepository.deleteByUserAndExhibition(user, exhibition);
    }
}
