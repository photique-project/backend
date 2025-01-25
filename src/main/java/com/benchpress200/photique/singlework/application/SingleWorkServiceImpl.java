package com.benchpress200.photique.singlework.application;


import com.benchpress200.photique.common.domain.dto.NewTagRequest;
import com.benchpress200.photique.common.domain.entity.Tag;
import com.benchpress200.photique.common.infrastructure.ImageUploader;
import com.benchpress200.photique.common.infrastructure.TagRepository;
import com.benchpress200.photique.singlework.domain.dto.NewSingleWorkRequest;
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

    public void createNewSingleWork(final NewSingleWorkRequest newSingleWorkRequest) {

        // 작성자 조회
        final Long writerId = newSingleWorkRequest.getWriterId();
        final User writer = userRepository.findById(writerId).orElseThrow(
                () -> new SingleWorkException("User with ID " + writerId + " is not found.", HttpStatus.NOT_FOUND)
        );

        // 이미지 업로드 & 단일작품 저장
        final String imageUrl = imageUploader.upload(newSingleWorkRequest.getImage(), imagePath);
        final SingleWork savedSingleWork = singleWorkRepository.save(
                newSingleWorkRequest.toSingleWorkEntity(writer, imageUrl));

        // 태그 데이터 유무 확인
        if (newSingleWorkRequest.hasTags()) {
            List<NewTagRequest> tags = newSingleWorkRequest.getTags();

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
            List<SingleWorkTag> singleWorkTags = newSingleWorkRequest.toSingleWorkTagEntities(savedSingleWork, allTags);

            // SingleWorkTag 엔티티 저장
            singleWorkTagRepository.saveAll(singleWorkTags);
        }
    }
}
