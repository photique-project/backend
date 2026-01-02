package com.benchpress200.photique.singlework.application.command.service;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationUserProviderPort;
import com.benchpress200.photique.image.domain.port.storage.ImageUploaderPort;
import com.benchpress200.photique.singlework.application.command.model.SingleWorkCreateCommand;
import com.benchpress200.photique.singlework.application.command.model.SingleWorkUpdateCommand;
import com.benchpress200.photique.singlework.application.command.port.in.DeleteSingleWorkUseCase;
import com.benchpress200.photique.singlework.application.command.port.in.PostSingleWorkUseCase;
import com.benchpress200.photique.singlework.application.command.port.in.UpdateSingleWorkDetailsUseCase;
import com.benchpress200.photique.singlework.application.command.port.out.event.SingleWorkEventPublishPort;
import com.benchpress200.photique.singlework.application.command.port.out.persistence.SingleWorkCommandPort;
import com.benchpress200.photique.singlework.application.command.port.out.persistence.SingleWorkTagCommandPort;
import com.benchpress200.photique.singlework.application.query.port.out.persistence.SingleWorkQueryPort;
import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkTag;
import com.benchpress200.photique.singlework.domain.enumeration.Aperture;
import com.benchpress200.photique.singlework.domain.enumeration.Category;
import com.benchpress200.photique.singlework.domain.enumeration.ISO;
import com.benchpress200.photique.singlework.domain.enumeration.ShutterSpeed;
import com.benchpress200.photique.singlework.domain.event.SingleWorkCreateEvent;
import com.benchpress200.photique.singlework.domain.event.SingleWorkImageUploadEvent;
import com.benchpress200.photique.singlework.domain.event.SingleWorkRemoveEvent;
import com.benchpress200.photique.singlework.domain.event.SingleWorkUpdateEvent;
import com.benchpress200.photique.singlework.domain.exception.SingleWorkNotFoundException;
import com.benchpress200.photique.singlework.domain.exception.SingleWorkNotOwnedException;
import com.benchpress200.photique.singlework.domain.exception.SingleWorkWriterNotFoundException;
import com.benchpress200.photique.tag.application.command.port.out.persistence.TagCommandPort;
import com.benchpress200.photique.tag.application.query.port.out.persistence.TagQueryPort;
import com.benchpress200.photique.tag.application.query.support.AbsentTags;
import com.benchpress200.photique.tag.application.query.support.ExistingTags;
import com.benchpress200.photique.tag.domain.entity.Tag;
import com.benchpress200.photique.user.application.query.port.out.persistence.UserQueryPort;
import com.benchpress200.photique.user.domain.entity.User;
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
public class SingleWorkCommandService implements
        PostSingleWorkUseCase,
        UpdateSingleWorkDetailsUseCase,
        DeleteSingleWorkUseCase {
    @Value("${cloud.aws.s3.path.single-work}")
    private String imagePath;

    private final AuthenticationUserProviderPort authenticationUserProviderPort;
    private final ImageUploaderPort imageUploaderPort;

    private final UserQueryPort userQueryPort;

    private final SingleWorkCommandPort singleWorkCommandPort;
    private final SingleWorkQueryPort singleWorkQueryPort;
    private final SingleWorkTagCommandPort singleWorkTagCommandPort;
    private final SingleWorkEventPublishPort singleWorkEventPublishPort;

    private final TagCommandPort tagCommandPort;
    private final TagQueryPort tagQueryPort;


    public void postSingleWork(SingleWorkCreateCommand command) {
        // 작성자 조회
        Long writerId = authenticationUserProviderPort.getCurrentUserId();
        User writer = userQueryPort.findById(writerId)
                .orElseThrow(() -> new SingleWorkWriterNotFoundException(writerId));

        // 이미지 업로드
        MultipartFile image = command.getImage();
        String imageUrl = imageUploaderPort.upload(image, imagePath);

        // 트랜잭션 롤백 시, S3 업로드한 이미지 삭제 이벤트 발행
        SingleWorkImageUploadEvent singleWorkImageUploadEvent = SingleWorkImageUploadEvent.of(imageUrl);
        singleWorkEventPublishPort.publishSingleWorkImageUploadEvent(singleWorkImageUploadEvent);

        // 단일작품 엔티티 저장
        SingleWork singleWork = command.toEntity(writer, imageUrl);
        singleWork = singleWorkCommandPort.save(singleWork);

        // 태그 엔티티 저장
        List<String> tagNames = command.getTags();
        attachTags(singleWork, tagNames);

        Long singleWorkId = singleWork.getId();

        // 트랜잭션 커밋 시 ES 동기화 & 작가 팔로워들에게 알림 생성 이벤트 발행
        SingleWorkCreateEvent singleWorkCreateEvent = SingleWorkCreateEvent.of(singleWorkId);
        singleWorkEventPublishPort.publishSingleWorkCreateEvent(singleWorkCreateEvent);
    }


    public void updateSingleWorkDetails(SingleWorkUpdateCommand command) {
        // 작품 조회
        Long singleWorkId = command.getSingleWorkId();
        SingleWork singleWork = singleWorkQueryPort.findByIdWithWriter(singleWorkId)
                .orElseThrow(() -> new SingleWorkNotFoundException(singleWorkId));

        Long writerId = authenticationUserProviderPort.getCurrentUserId();

        // 요청한 유저가 해당 단일작품의 주인이 아닐 때
        if (!singleWork.isOwnedBy(writerId)) {
            throw new SingleWorkNotOwnedException();
        }

        // 제목 업데이트
        if (command.isUpdateTitle()) {
            String titleToUpdate = command.getTitle();
            singleWork.updateTitle(titleToUpdate);
        }

        // 설명 업데이트
        if (command.isUpdateDescription()) {
            String descriptionToUpdate = command.getDescription();
            singleWork.updateDescription(descriptionToUpdate);
        }

        // 카메라 업데이트
        if (command.isUpdateCamera()) {
            String cameraToUpdate = command.getCamera();
            singleWork.updateCamera(cameraToUpdate);
        }

        // 렌즈 업데이트
        if (command.isUpdateLens()) {
            String lensToUpdate = command.getLens();
            singleWork.updateLens(lensToUpdate);
        }

        // 조리개 값 업데이트
        if (command.isUpdateAperture()) {
            Aperture apertureToUpdate = command.getAperture();
            singleWork.updateAperture(apertureToUpdate);
        }

        // 셔터스피드 값 업데이트
        if (command.isUpdateShutterSpeed()) {
            ShutterSpeed shutterSpeedToUpdate = command.getShutterSpeed();
            singleWork.updateShutterSpeed(shutterSpeedToUpdate);
        }

        // ISO 값 업데이트
        if (command.isUpdateIso()) {
            ISO isoToUpdate = command.getIso();
            singleWork.updateIso(isoToUpdate);
        }

        // 카테고리 업데이트
        if (command.isUpdateCategory()) {
            Category categoryToUpdate = command.getCategory();
            singleWork.updateCategory(categoryToUpdate);
        }

        // 위치 업데이트
        if (command.isUpdateLocation()) {
            String locationToUpdate = command.getLocation();
            singleWork.updateLocation(locationToUpdate);
        }

        // 촬영날짜 업데이
        if (command.isUpdateDate()) {
            LocalDate dateToUpdate = command.getDate();
            singleWork.updateDate(dateToUpdate);
        }

        // 태그 업데이트
        if (command.isUpdateTags()) {
            List<String> tagNamesToUpdate = command.getTags();

            // 해당 작품의 기존 태그 모두 삭제 (벌크연산)
            singleWorkTagCommandPort.deleteBySingleWork(singleWork);

            // 업데이트 태그 등록
            attachTags(singleWork, tagNamesToUpdate);
        }

        // 단일작품 MySQL-ES 동기화 이벤트 발행
        if (command.isUpdate()) {
            SingleWorkUpdateEvent event = SingleWorkUpdateEvent.of(singleWorkId);
            singleWorkEventPublishPort.publishSingleWorkUpdateEvent(event);
        }
    }

    // FIXME: 삭제 처리할 때 관련 댓글 처리 어떻게 할지, deletedAt 이 null 아닌 데이터를 어느 시점에 어떻게 처리할지 고민
    public void deleteSingleWork(Long singleWorkId) {
        // 작품 조회
        singleWorkQueryPort.findActiveByIdWithWriter(singleWorkId)
                .ifPresent(singleWork -> { // 존재한다면 삭제 처리
                    Long writerId = authenticationUserProviderPort.getCurrentUserId();

                    // 요청한 유저가 해당 단일작품의 주인이 아닐 때
                    if (!singleWork.isOwnedBy(writerId)) {
                        throw new SingleWorkNotOwnedException();
                    }

                    singleWork.remove();

                    // 단일작품 MySQL-ES 동기화 이벤트 발행
                    SingleWorkRemoveEvent event = SingleWorkRemoveEvent.of(singleWorkId);
                    singleWorkEventPublishPort.publishSingleWorkRemoveEvent(event);
                });

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
            SingleWorkTag singleWorkTag = SingleWorkTag.of(singleWork, tag);
            singleWorkTagCommandPort.save(singleWorkTag);
        }
    }
}
