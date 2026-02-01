package com.benchpress200.photique.exhibition.application.command.service;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationUserProviderPort;
import com.benchpress200.photique.exhibition.application.command.model.ExhibitionCreateCommand;
import com.benchpress200.photique.exhibition.application.command.model.ExhibitionUpdateCommand;
import com.benchpress200.photique.exhibition.application.command.model.ExhibitionWorkCreateCommand;
import com.benchpress200.photique.exhibition.application.command.model.ExhibitionWorkUpdateCommand;
import com.benchpress200.photique.exhibition.application.command.port.in.DeleteExhibitionUseCase;
import com.benchpress200.photique.exhibition.application.command.port.in.OpenExhibitionUseCase;
import com.benchpress200.photique.exhibition.application.command.port.in.UpdateExhibitionDetailsUseCase;
import com.benchpress200.photique.exhibition.application.command.port.out.ExhibitionCommandPort;
import com.benchpress200.photique.exhibition.application.command.port.out.ExhibitionEventPublishPort;
import com.benchpress200.photique.exhibition.application.command.port.out.ExhibitionTagCommandPort;
import com.benchpress200.photique.exhibition.application.command.port.out.ExhibitionWorkCommandPort;
import com.benchpress200.photique.exhibition.application.query.port.out.persistence.ExhibitionQueryPort;
import com.benchpress200.photique.exhibition.application.query.port.out.persistence.ExhibitionWorkQueryPort;
import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionTag;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionWork;
import com.benchpress200.photique.exhibition.domain.event.ExhibitionUpdateEvent;
import com.benchpress200.photique.exhibition.domain.event.ExhibitionWorkImageUploadEvent;
import com.benchpress200.photique.exhibition.domain.exception.ExhibitionNotFoundException;
import com.benchpress200.photique.exhibition.domain.exception.ExhibitionNotOwnedException;
import com.benchpress200.photique.exhibition.domain.exception.ExhibitionWorkDuplicatedDisplayOrderException;
import com.benchpress200.photique.exhibition.domain.exception.ExhibitionWorkNotFoundException;
import com.benchpress200.photique.image.domain.port.storage.ImageUploaderPort;
import com.benchpress200.photique.outbox.application.factory.OutboxEventFactory;
import com.benchpress200.photique.outbox.application.port.out.persistence.OutboxEventPort;
import com.benchpress200.photique.outbox.domain.entity.OutboxEvent;
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
        OpenExhibitionUseCase,
        UpdateExhibitionDetailsUseCase,
        DeleteExhibitionUseCase {
    @Value("${cloud.aws.s3.path.exhibition}")
    private String imagePath;

    private final AuthenticationUserProviderPort authenticationUserProviderPort;
    private final UserQueryPort userQueryPort;
    private final ImageUploaderPort imageUploaderPort;

    private final ExhibitionEventPublishPort exhibitionEventPublishPort;
    private final ExhibitionCommandPort exhibitionCommandPort;
    private final ExhibitionQueryPort exhibitionQueryPort;
    private final ExhibitionTagCommandPort exhibitionTagCommandPort;
    private final ExhibitionWorkCommandPort exhibitionWorkCommandPort;
    private final ExhibitionWorkQueryPort exhibitionWorkQueryPort;

    private final TagCommandPort tagCommandPort;
    private final TagQueryPort tagQueryPort;

    private final OutboxEventFactory outboxEventFactory;
    private final OutboxEventPort outboxEventPort;


    @Override
    public void openExhibition(ExhibitionCreateCommand command) {
        // 작가 조회
        Long writerId = authenticationUserProviderPort.getCurrentUserId();
        User writer = userQueryPort.findByIdAndDeletedAtIsNull(writerId)
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

        // 아웃박스 이벤트 발행 -> ES 동기화 & 팔로워 알림 생성 배치 처리
        OutboxEvent outboxEvent = outboxEventFactory.exhibitionCreated(exhibition, tagNames);
        outboxEventPort.save(outboxEvent);

        // TODO: 이후 알림 아웃박스 이벤트 생성 코드 추가 후 이벤트 발행 포트, 어댑터, 리스너 코드 제거
    }

    @Override
    public void updateExhibitionDetailsUpdate(ExhibitionUpdateCommand command) {
        // 전시회 조회
        Long exhibitionId = command.getExhibitionId();
        Exhibition exhibition = exhibitionQueryPort.findByIdAndDeletedAtIsNull(exhibitionId)
                .orElseThrow(() -> new ExhibitionNotFoundException(exhibitionId));

        Long writerId = authenticationUserProviderPort.getCurrentUserId();

        // 요청한 유저가 해당 전시회의 주인이 아닐 때
        if (!exhibition.isOwnedBy(writerId)) {
            throw new ExhibitionNotOwnedException();
        }

        // 제목 업데이트
        if (command.isUpdateTitle()) {
            String titleToUpdate = command.getTitle();
            exhibition.updateTitle(titleToUpdate);
        }

        // 설명 업데이트
        if (command.isUpdateDescription()) {
            String descriptionToUpdate = command.getDescription();
            exhibition.updateDescription(descriptionToUpdate);
        }

        // 카드 색상 업데이트
        if (command.isUpdateCardColor()) {
            String cardColorToUpdate = command.getCardColor();
            exhibition.updateCardColor(cardColorToUpdate);
        }

        // 태그 업데이트
        if (command.isUpdateTags()) {
            List<String> tagNamesToUpdate = command.getTags();

            // 해당 전시회의 기존 태그 모두 삭제 (벌크연산)
            exhibitionTagCommandPort.deleteByExhibition(exhibition);

            // 업데이트 태그 등록
            attachTags(exhibition, tagNamesToUpdate);
        }

        // 전시회 개별 작품 업데이트
        if (command.isUpdateWorks()) {
            List<ExhibitionWorkUpdateCommand> workCommands = command.getWorks();

            workCommands.forEach(workCommand -> {
                Long id = workCommand.getId();
                ExhibitionWork exhibitionWork = exhibitionWorkQueryPort.findById(id)
                        .orElseThrow(() -> new ExhibitionWorkNotFoundException(id));

                Integer displayOrder = workCommand.getDisplayOrder();
                String title = workCommand.getTitle();
                String description = workCommand.getDescription();

                exhibitionWork.updateDisplayOrder(displayOrder);
                exhibitionWork.updateTitle(title);
                exhibitionWork.updateDescription(description);
            });

            // 작품들 모두 조회해서 중복 order 없는지 확인
            List<ExhibitionWork> exhibitionWorks = exhibitionWorkQueryPort.findByExhibition(exhibition);

            if (exhibitionWorks.stream()
                    .map(ExhibitionWork::getDisplayOrder)
                    .distinct()
                    .count() != exhibitionWorks.size()
            ) {
                throw new ExhibitionWorkDuplicatedDisplayOrderException();
            }
        }

        // 전시회 MySQL-ES 동기화 이벤트 발행
        if (command.isUpdate()) {
            ExhibitionUpdateEvent event = ExhibitionUpdateEvent.of(exhibitionId);
            exhibitionEventPublishPort.publishExhibitionUpdateEvent(event);
        }
    }

    // FIXME: deletedAt = null 아닌 데이터를 어느 시점에 어떻게 처리할지 고민
    @Override
    public void deleteExhibition(Long exhibitionId) {
        exhibitionQueryPort.findByIdAndDeletedAtIsNull(exhibitionId)
                .ifPresent(exhibition -> {
                    Long writerId = authenticationUserProviderPort.getCurrentUserId();

                    // 요청한 유저가 해당 전시회의 주인이 아닐 때
                    if (!exhibition.isOwnedBy(writerId)) {
                        throw new ExhibitionNotOwnedException();
                    }

                    exhibition.remove();

                    // 아웃박스 이벤트 발행 -> 비동기 이벤트
                    OutboxEvent outboxEvent = outboxEventFactory.exhibitionDeleted(exhibition);
                    outboxEventPort.save(outboxEvent);
                });
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
