package com.benchpress200.photique.singlework.application;

import com.benchpress200.photique.auth.domain.port.AuthenticationUserProviderPort;
import com.benchpress200.photique.image.domain.event.ImageEventPublisher;
import com.benchpress200.photique.image.domain.port.ImageUploaderPort;
import com.benchpress200.photique.notification.domain.event.NotificationEventPublisher;
import com.benchpress200.photique.singlework.application.command.NewSingleWorkCommand;
import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkTag;
import com.benchpress200.photique.singlework.domain.event.SingleWorkSearchEventPublisher;
import com.benchpress200.photique.singlework.domain.exception.SingleWorkWriterNotFoundException;
import com.benchpress200.photique.singlework.domain.repository.SingleWorkRepository;
import com.benchpress200.photique.singlework.domain.repository.SingleWorkTagRepository;
import com.benchpress200.photique.tag.domain.entity.Tag;
import com.benchpress200.photique.tag.domain.repository.TagRepository;
import com.benchpress200.photique.tag.domain.vo.AbsentTags;
import com.benchpress200.photique.tag.domain.vo.ExistingTags;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class SingleWorkCommandService {
    private final NotificationEventPublisher notificationEventPublisher;
    @Value("${cloud.aws.s3.path.single-work}")
    private String imagePath;

    private final AuthenticationUserProviderPort authenticationUserProviderPort;
    private final ImageUploaderPort imageUploaderPort;
    private final UserRepository userRepository;
    private final SingleWorkRepository singleWorkRepository;
    private final SingleWorkTagRepository singleWorkTagRepository;
    private final TagRepository tagRepository;

    private final ImageEventPublisher imageEventPublisher;
    private final SingleWorkSearchEventPublisher singleWorkSearchEventPublisher;

    public void postSingleWork(NewSingleWorkCommand newSingleWorkCommand) {
        // 작성자 조회
        Long writerId = authenticationUserProviderPort.getCurrentUserId();
        User writer = userRepository.findById(writerId)
                .orElseThrow(() -> new SingleWorkWriterNotFoundException(writerId));

        // 이미지 업로드
        MultipartFile image = newSingleWorkCommand.getImage();
        String imageUrl = imageUploaderPort.upload(image, imagePath);

        // 트랜잭션 롤백 시, S3 업로드한 이미지 삭제 이벤트 수행
        imageEventPublisher.publishImageDeleteEventIfRollback(imageUrl);

        // 단일작품 엔티티 저장
        SingleWork singleWork = newSingleWorkCommand.toEntity(writer, imageUrl);
        singleWork = singleWorkRepository.save(singleWork);

        // 태그 엔티티 저장
        List<String> tagNames = newSingleWorkCommand.getTags();
        List<Tag> tags = tagRepository.findByNameIn(tagNames);
        ExistingTags existingTags = ExistingTags.of(tags); // 존재하는 태그 일급 컬렉션

        List<String> absentTagNames = existingTags.findAbsent(tagNames); // 존재하지 않는 태그 이름 추출
        AbsentTags absentTags = AbsentTags.of(absentTagNames); // 존재하지 않는 태그 일급 컬렉션
        List<Tag> savedTags = tagRepository.saveAll(absentTags.values()); // 영속화

        existingTags = existingTags.merge(savedTags); // 기존에 조회했던 태그와 병합
        List<Tag> allTags = existingTags.values();

        for (Tag tag : allTags) {
            singleWorkTagRepository.save(SingleWorkTag.of(singleWork, tag));
        }

        // ES 저장
        Long singleWorkId = singleWork.getId();
        singleWorkSearchEventPublisher.publishSingleWorkSearchCreationEventIfCommit(singleWorkId);

        // 알림 생성
        notificationEventPublisher.publishNewSingleWorkNotificationEventIfCommit(singleWorkId);
    }
}
