package com.benchpress200.photique.singlework.application;


import com.benchpress200.photique.common.domain.dto.NewTagRequest;
import com.benchpress200.photique.common.domain.entity.Tag;
import com.benchpress200.photique.common.infrastructure.ImageUploader;
import com.benchpress200.photique.common.infrastructure.TagRepository;
import com.benchpress200.photique.singlework.domain.dto.SingleWorkCreateRequest;
import com.benchpress200.photique.singlework.domain.dto.SingleWorkDetailResponse;
import com.benchpress200.photique.singlework.domain.dto.SingleWorkSearchRequest;
import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkTag;
import com.benchpress200.photique.singlework.exception.SingleWorkException;
import com.benchpress200.photique.singlework.infrastructure.SingleWorkRepository;
import com.benchpress200.photique.singlework.infrastructure.SingleWorkTagRepository;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.infrastructure.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class SingleWorkServiceImpl implements SingleWorkService {
    @Value("${cloud.aws.s3.path.single-work}")
    private String imagePath;

    private final ImageUploader imageUploader;
    private final UserRepository userRepository;
    private final SingleWorkRepository singleWorkRepository;
    private final SingleWorkTagRepository singleWorkTagRepository;
    private final TagRepository tagRepository;

    @Override
    public void createNewSingleWork(final SingleWorkCreateRequest SingleWorkCreateRequest) {

        // 작성자 조회
        final Long writerId = SingleWorkCreateRequest.getWriterId();
        final User writer = userRepository.findById(writerId).orElseThrow(
                () -> new SingleWorkException("User with ID " + writerId + " is not found.", HttpStatus.NOT_FOUND)
        );

        // 이미지 업로드 & 단일작품 저장
        final String imageUrl = imageUploader.upload(SingleWorkCreateRequest.getImage(), imagePath);
        final SingleWork savedSingleWork = singleWorkRepository.save(
                SingleWorkCreateRequest.toSingleWorkEntity(writer, imageUrl));

        // 태그 데이터 유무 확인
        if (SingleWorkCreateRequest.hasTags()) {
            List<NewTagRequest> tags = SingleWorkCreateRequest.getTags();

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
            List<SingleWorkTag> singleWorkTags = SingleWorkCreateRequest.toSingleWorkTagEntities(savedSingleWork,
                    allTags);

            // SingleWorkTag 엔티티 저장
            singleWorkTagRepository.saveAll(singleWorkTags);
        }
    }

    @Override
    public void getSingleWorks(final SingleWorkSearchRequest singleWorkSearchRequest) {
        // 값이 없을 때 빈 문자열 넘어옴

        // q 제목, 태그 가반 검색
        // target
        // sort
        // category
        // page
        // size

        // querydsl를 사용해서 동적 쿼리작성 페이징도 사용
        // 검색 프로세스
        // target 확인 -> 검색 대상
        // 값이 비었거나 작품이라면)
        // q 확인 -> 키워드
        // 키워드 없으면 전체 검색
        // 키워드 있으면 키워드에 있는 단어를 가진 타이틀 혹은 태그 포함 검색
        // sort 확인 -> 정렬 기준
        // sort 없으면 최신순
        // 있으면 해당 기준으로 정렬
        // category 확인 -> 다수선택가능 , or 연산
        // category 없으면 전체 검색
        // 있으면 or연산검색

        // 작가라면)
        // q 확인 -> 키워드
        // 키워드 없으면 전체 검색
        // 키워드 있으면 키워드에 있는 단어를 가진 작가의 작품 검색
        // sort 확인 -> 정렬 기준
        // sort 없으면 최신순
        // 있으면 해당 기준으로 정렬
        // category 확인 -> 다수선택가능 , or 연산
        // category 없으면 전체 검색
        // 있으면 or연산검색
    }

    @Override
    public SingleWorkDetailResponse getSingleWorkDetail(final Long singleWorkId) {
        SingleWork singleWork = singleWorkRepository.findById(singleWorkId).orElseThrow(
                () -> new SingleWorkException("SingleWork with ID " + singleWorkId + " is not found.",
                        HttpStatus.NOT_FOUND)
        );

        List<SingleWorkTag> singleWorkTags = singleWorkTagRepository.findBySingleWorkId(singleWorkId);

        List<Tag> tags = singleWorkTags.stream()
                .map(SingleWorkTag::getTag)
                .toList();

        return SingleWorkDetailResponse.from(
                singleWork,
                tags
        );
    }
}
