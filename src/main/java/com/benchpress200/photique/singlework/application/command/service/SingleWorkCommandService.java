package com.benchpress200.photique.singlework.application.command.service;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationUserProviderPort;
import com.benchpress200.photique.image.domain.event.UploadImageEvent;
import com.benchpress200.photique.image.domain.port.event.ImageEventPublishPort;
import com.benchpress200.photique.image.domain.port.storage.ImageUploaderPort;
import com.benchpress200.photique.notification.domain.event.NotificationEventPublisher;
import com.benchpress200.photique.singlework.application.command.model.SingleWorkCreateCommand;
import com.benchpress200.photique.singlework.application.command.model.SingleWorkUpdateCommand;
import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkTag;
import com.benchpress200.photique.singlework.domain.enumeration.Aperture;
import com.benchpress200.photique.singlework.domain.enumeration.Category;
import com.benchpress200.photique.singlework.domain.enumeration.ISO;
import com.benchpress200.photique.singlework.domain.enumeration.ShutterSpeed;
import com.benchpress200.photique.singlework.domain.event.SingleWorkSearchEventPublisher;
import com.benchpress200.photique.singlework.domain.exception.SingleWorkNotFoundException;
import com.benchpress200.photique.singlework.domain.exception.SingleWorkNotOwnedException;
import com.benchpress200.photique.singlework.domain.exception.SingleWorkWriterNotFoundException;
import com.benchpress200.photique.singlework.infrastructure.persistence.jpa.SingleWorkRepository;
import com.benchpress200.photique.singlework.infrastructure.persistence.jpa.SingleWorkTagRepository;
import com.benchpress200.photique.tag.application.command.port.out.persistence.TagCommandPort;
import com.benchpress200.photique.tag.application.query.port.out.persistence.TagQueryPort;
import com.benchpress200.photique.tag.application.query.support.AbsentTags;
import com.benchpress200.photique.tag.application.query.support.ExistingTags;
import com.benchpress200.photique.tag.domain.entity.Tag;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.infrastructure.persistence.jpa.UserRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class SingleWorkCommandService {
    @Value("${cloud.aws.s3.path.single-work}")
    private String imagePath;

    private final NotificationEventPublisher notificationEventPublisher;
    private final AuthenticationUserProviderPort authenticationUserProviderPort;
    private final ImageUploaderPort imageUploaderPort;
    private final UserRepository userRepository;
    private final SingleWorkRepository singleWorkRepository;
    private final SingleWorkTagRepository singleWorkTagRepository;

    private final TagCommandPort tagCommandPort;
    private final TagQueryPort tagQueryPort;

    private final ImageEventPublishPort imageEventPublishPort;
    private final SingleWorkSearchEventPublisher singleWorkSearchEventPublisher;

    public void postSingleWork(SingleWorkCreateCommand singleWorkCreateCommand) {
        // 작성자 조회
        Long writerId = authenticationUserProviderPort.getCurrentUserId();
        User writer = userRepository.findById(writerId)
                .orElseThrow(() -> new SingleWorkWriterNotFoundException(writerId));

        // 이미지 업로드
        MultipartFile image = singleWorkCreateCommand.getImage();
        String imageUrl = imageUploaderPort.upload(image, imagePath);

        // 트랜잭션 롤백 시, S3 업로드한 이미지 삭제 이벤트 수행
        UploadImageEvent uploadImageEvent = UploadImageEvent.of(imageUrl);
        imageEventPublishPort.publishUploadImageEvent(uploadImageEvent);

        // 단일작품 엔티티 저장
        SingleWork singleWork = singleWorkCreateCommand.toEntity(writer, imageUrl);
        singleWork = singleWorkRepository.save(singleWork);

        // 태그 엔티티 저장
        List<String> tagNames = singleWorkCreateCommand.getTags();
        attachTags(singleWork, tagNames);

        // ES 저장
        Long singleWorkId = singleWork.getId();
        singleWorkSearchEventPublisher.publishCreateSingleWorkSearchEvent(singleWorkId);

        // 알림 생성
        notificationEventPublisher.publishCreateSingleWorkNotificationEvent(singleWorkId);
    }

    public void updateSingleWorkDetails(SingleWorkUpdateCommand singleWorkUpdateCommand) {
        // 작품 조회
        Long singleWorkId = singleWorkUpdateCommand.getSingleWorkId();
        SingleWork singleWork = singleWorkRepository.findWithWriter(singleWorkId)
                .orElseThrow(() -> new SingleWorkNotFoundException(singleWorkId));

        Long writerId = authenticationUserProviderPort.getCurrentUserId();

        // 요청한 유저가 해당 단일작품의 주인이 아닐 때
        if (!singleWork.isOwnedBy(writerId)) {
            throw new SingleWorkNotOwnedException();
        }

        // 제목 업데이트
        if (singleWorkUpdateCommand.isUpdateTitle()) {
            String titleToUpdate = singleWorkUpdateCommand.getTitle();
            singleWork.updateTitle(titleToUpdate);
        }

        // 설명 업데이트
        if (singleWorkUpdateCommand.isUpdateDescription()) {
            String descriptionToUpdate = singleWorkUpdateCommand.getDescription();
            singleWork.updateDescription(descriptionToUpdate);
        }

        // 카메라 업데이트
        if (singleWorkUpdateCommand.isUpdateCamera()) {
            String cameraToUpdate = singleWorkUpdateCommand.getCamera();
            singleWork.updateCamera(cameraToUpdate);
        }

        // 렌즈 업데이트
        if (singleWorkUpdateCommand.isUpdateLens()) {
            String lensToUpdate = singleWorkUpdateCommand.getLens();
            singleWork.updateLens(lensToUpdate);
        }

        // 조리개 값 업데이트
        if (singleWorkUpdateCommand.isUpdateAperture()) {
            Aperture apertureToUpdate = singleWorkUpdateCommand.getAperture();
            singleWork.updateAperture(apertureToUpdate);
        }

        // 셔터스피드 값 업데이트
        if (singleWorkUpdateCommand.isUpdateShutterSpeed()) {
            ShutterSpeed shutterSpeedToUpdate = singleWorkUpdateCommand.getShutterSpeed();
            singleWork.updateShutterSpeed(shutterSpeedToUpdate);
        }

        // ISO 값 업데이트
        if (singleWorkUpdateCommand.isUpdateIso()) {
            ISO isoToUpdate = singleWorkUpdateCommand.getIso();
            singleWork.updateIso(isoToUpdate);
        }

        // 카테고리 업데이트
        if (singleWorkUpdateCommand.isUpdateCategory()) {
            Category categoryToUpdate = singleWorkUpdateCommand.getCategory();
            singleWork.updateCategory(categoryToUpdate);
        }

        // 위치 업데이트
        if (singleWorkUpdateCommand.isUpdateLocation()) {
            String locationToUpdate = singleWorkUpdateCommand.getLocation();
            singleWork.updateLocation(locationToUpdate);
        }

        // 촬영날짜 업데이트
        if (singleWorkUpdateCommand.isUpdateDate()) {
            LocalDate dateToUpdate = singleWorkUpdateCommand.getDate();
            singleWork.updateDate(dateToUpdate);
        }

        // 태그 업데이트
        if (singleWorkUpdateCommand.isUpdateTags()) {
            List<String> tagNamesToUpdate = singleWorkUpdateCommand.getTags();

            // 해당 작품의 기존 태그 모두 삭제 (벌크연산)
            singleWorkTagRepository.deleteBySingleWork(singleWork);

            // 업데이트 태그 등록
            attachTags(singleWork, tagNamesToUpdate);
        }

        // 단일작품 MySQL-ES 동기화 이벤트 발행
        if (singleWorkUpdateCommand.isUpdate()) {
            singleWorkSearchEventPublisher.publishUpdateSingleWorkSearchEvent(singleWorkId);
        }
    }

    public void removeSingleWork(Long singleWorkId) {
        // 작품 조회
        SingleWork singleWork = singleWorkRepository.findWithWriter(singleWorkId)
                .orElseThrow(() -> new SingleWorkNotFoundException(singleWorkId));

        Long writerId = authenticationUserProviderPort.getCurrentUserId();

        // 요청한 유저가 해당 단일작품의 주인이 아닐 때
        if (!singleWork.isOwnedBy(writerId)) {
            throw new SingleWorkNotOwnedException();
        }

        singleWork.remove();

        // 단일작품 MySQL-ES 동기화 이벤트 발행
        singleWorkSearchEventPublisher.publishRemoveSingleWorkSearchEvent(singleWorkId);
    }

    private void attachTags(SingleWork singleWork, List<String> tagNames) {
        List<Tag> tags = tagQueryPort.findByNameIn(tagNames);
        ExistingTags existingTags = ExistingTags.of(tags); // 존재하는 태그 일급 컬렉션

        List<String> absentTagNames = existingTags.findAbsent(tagNames); // 존재하지 않는 태그 이름 추출
        AbsentTags absentTags = AbsentTags.of(absentTagNames); // 존재하지 않는 태그 일급 컬렉션
        List<Tag> savedTags = tagCommandPort.saveAll(absentTags.values()); // 영속화

        existingTags = existingTags.merge(savedTags); // 기존에 조회했던 태그와 병합
        List<Tag> allTags = existingTags.values();

        for (Tag tag : allTags) {
            singleWorkTagRepository.save(SingleWorkTag.of(singleWork, tag));
        }
    }
}
