package com.benchpress200.photique.exhibition.application.command.service;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationUserProviderPort;
import com.benchpress200.photique.exhibition.application.command.model.ExhibitionCreateCommand;
import com.benchpress200.photique.exhibition.application.command.model.ExhibitionWorkCreateCommand;
import com.benchpress200.photique.exhibition.application.command.port.in.OpenExhibitionUseCase;
import com.benchpress200.photique.exhibition.application.command.port.out.ExhibitionCommandPort;
import com.benchpress200.photique.exhibition.application.command.port.out.ExhibitionEventPublishPort;
import com.benchpress200.photique.exhibition.application.command.port.out.ExhibitionTagCommandPort;
import com.benchpress200.photique.exhibition.application.command.port.out.ExhibitionWorkCommandPort;
import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionTag;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionWork;
import com.benchpress200.photique.exhibition.domain.event.ExhibitionCreateEvent;
import com.benchpress200.photique.exhibition.domain.event.ExhibitionWorkImageUploadEvent;
import com.benchpress200.photique.image.domain.port.storage.ImageUploaderPort;
import com.benchpress200.photique.tag.application.command.port.out.persistence.TagCommandPort;
import com.benchpress200.photique.tag.application.query.port.out.persistence.TagQueryPort;
import com.benchpress200.photique.tag.application.query.support.AbsentTags;
import com.benchpress200.photique.tag.application.query.support.ExistingTags;
import com.benchpress200.photique.tag.domain.entity.Tag;
import com.benchpress200.photique.user.application.query.port.out.persistence.UserQueryPort;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.exception.UserNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class ExhibitionCommandService implements
        OpenExhibitionUseCase {
    @Value("${cloud.aws.s3.path.exhibition}")
    private String imagePath;

    private final AuthenticationUserProviderPort authenticationUserProviderPort;
    private final UserQueryPort userQueryPort;
    private final ImageUploaderPort imageUploaderPort;

    private final ExhibitionEventPublishPort exhibitionEventPublishPort;
    private final ExhibitionCommandPort exhibitionCommandPort;
    private final ExhibitionWorkCommandPort exhibitionWorkCommandPort;
    private final ExhibitionTagCommandPort exhibitionTagCommandPort;

    private final TagCommandPort tagCommandPort;
    private final TagQueryPort tagQueryPort;

    @Override
    public void openExhibition(ExhibitionCreateCommand command) {
        // 작가 조회
        Long writerId = authenticationUserProviderPort.getCurrentUserId();
        User writer = userQueryPort.findActiveById(writerId)
                .orElseThrow(() -> new UserNotFoundException(writerId));

        // 전시회 엔티티 저장
        Exhibition exhibition = command.toEntity(writer);
        Exhibition savedExhibition = exhibitionCommandPort.save(exhibition); // 람다식에 넣기 위해 재할당

        // 작품들 이미지 업로드하고 개별 작품 엔티티 저장, 이미지 롤백 이벤트 발행
        List<ExhibitionWorkCreateCommand> workCommands = command.getWorks();
        workCommands.forEach(workCommand -> {
            MultipartFile image = workCommand.getImage();
            String imageUrl = imageUploaderPort.upload(image, imagePath);

            // 트랜잭션 롤백 시, S3 업로드한 이미지 삭제 이벤트 발행
            ExhibitionWorkImageUploadEvent event = ExhibitionWorkImageUploadEvent.of(imageUrl);
            exhibitionEventPublishPort.publishExhibitionWorkImageUploadEvent(event);

            // 전시회 개별 작품 저장
            ExhibitionWork work = workCommand.toEntity(savedExhibition, imageUrl);
            exhibitionWorkCommandPort.save(work);
        });

        // 태그 엔티티 저장
        List<String> tagNames = command.getTags();
        attachTags(exhibition, tagNames);

        // 트랜잭션 커밋 시 ES 동기화 & 작가 팔로워들에게 알림 생성 이벤트 발행
        Long exhibitionId = exhibition.getId();
        ExhibitionCreateEvent event = ExhibitionCreateEvent.of(exhibitionId);
        exhibitionEventPublishPort.publishExhibitionCreateEvent(event);
    }

    private void attachTags(Exhibition exhibition, List<String> tagNames) {
        List<Tag> tags = tagQueryPort.findByNameIn(tagNames);
        ExistingTags existingTags = ExistingTags.of(tags); // 존재하는 태그 일급 컬렉션

        List<String> absentTagNames = existingTags.findAbsent(tagNames); // 존재하지 않는 태그 이름 추출
        AbsentTags absentTags = AbsentTags.of(absentTagNames); // 존재하지 않는 태그 일급 컬렉션
        List<Tag> savedTags = tagCommandPort.saveAll(absentTags.values()); // 영속화

        existingTags = existingTags.merge(savedTags); // 기존에 조회했던 태그와 병합
        List<Tag> allTags = existingTags.values();

        for (Tag tag : allTags) {
            ExhibitionTag exhibitionTag = ExhibitionTag.of(exhibition, tag);
            exhibitionTagCommandPort.save(exhibitionTag);
        }
    }
}
