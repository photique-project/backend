package com.benchpress200.photique.exhibition.application.command.service;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationUserProviderPort;
import com.benchpress200.photique.exhibition.application.command.model.ExhibitionCommentCreateCommand;
import com.benchpress200.photique.exhibition.application.command.model.ExhibitionCommentUpdateCommand;
import com.benchpress200.photique.exhibition.application.command.port.in.CreateExhibitionCommentUseCase;
import com.benchpress200.photique.exhibition.application.command.port.in.DeleteExhibitionCommentUseCase;
import com.benchpress200.photique.exhibition.application.command.port.in.UpdateExhibitionCommentUseCase;
import com.benchpress200.photique.exhibition.application.command.port.out.ExhibitionCommentCommandPort;
import com.benchpress200.photique.exhibition.application.command.port.out.ExhibitionEventPublishPort;
import com.benchpress200.photique.exhibition.application.query.port.out.ExhibitionCommentQueryPort;
import com.benchpress200.photique.exhibition.application.query.port.out.ExhibitionQueryPort;
import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionComment;
import com.benchpress200.photique.exhibition.domain.event.ExhibitionCommentCreateEvent;
import com.benchpress200.photique.exhibition.domain.exception.ExhibitionCommentNotFoundException;
import com.benchpress200.photique.exhibition.domain.exception.ExhibitionCommentNotOwnedException;
import com.benchpress200.photique.exhibition.domain.exception.ExhibitionNotFoundException;
import com.benchpress200.photique.user.application.query.port.out.persistence.UserQueryPort;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.exception.UserNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class ExhibitionCommentCommandService implements
        CreateExhibitionCommentUseCase,
        UpdateExhibitionCommentUseCase,
        DeleteExhibitionCommentUseCase {
    private final AuthenticationUserProviderPort authenticationUserProvider;
    private final UserQueryPort userQueryPort;

    private final ExhibitionQueryPort exhibitionQueryPort;
    private final ExhibitionCommentQueryPort exhibitionCommentQueryPort;
    private final ExhibitionCommentCommandPort exhibitionCommentCommandPort;
    private final ExhibitionEventPublishPort exhibitionEventPublishPort;


    @Override
    public void createExhibitionComment(ExhibitionCommentCreateCommand command) {
        // 감상평 작성 유저 조회
        Long currentUserId = authenticationUserProvider.getCurrentUserId();
        User writer = userQueryPort.findActiveById(currentUserId)
                .orElseThrow(() -> new UserNotFoundException(currentUserId));

        // 감상평 작성 전시회 조회
        Long exhibitionId = command.getExhibitionId();
        Exhibition exhibition = exhibitionQueryPort.findActiveById(exhibitionId)
                .orElseThrow(() -> new ExhibitionNotFoundException(exhibitionId));

        // 감상평 생성 및 저장
        ExhibitionComment exhibitionComment = command.toEntity(writer, exhibition);
        exhibitionCommentCommandPort.save(exhibitionComment);

        // 감상평 추가 알림 생성 이벤트 발행
        ExhibitionCommentCreateEvent event = ExhibitionCommentCreateEvent.of(exhibitionId);
        exhibitionEventPublishPort.publishExhibitionCommentCreateEvent(event);
    }

    @Override
    public void updateExhibitionComment(ExhibitionCommentUpdateCommand command) {
        // 감상평 조회
        Long exhibitionCommentId = command.getCommentId();
        ExhibitionComment exhibitionComment = exhibitionCommentQueryPort.findById(exhibitionCommentId)
                .orElseThrow(() -> new ExhibitionCommentNotFoundException(exhibitionCommentId));

        // 작성자 맞는지 확인
        Long writerId = authenticationUserProvider.getCurrentUserId();

        if (!exhibitionComment.isOwnedBy(writerId)) {
            throw new ExhibitionCommentNotOwnedException();
        }

        // 감상평 업데이트
        String content = command.getContent();
        exhibitionComment.updateContent(content);
    }

    @Override
    public void deleteExhibitionComment(Long exhibitionCommentId) {
        exhibitionCommentQueryPort.findById(exhibitionCommentId)
                .ifPresent(exhibitionComment -> {
                    Long writerId = authenticationUserProvider.getCurrentUserId();

                    if (!exhibitionComment.isOwnedBy(writerId)) {
                        throw new ExhibitionCommentNotOwnedException();
                    }

                    exhibitionCommentCommandPort.delete(exhibitionComment);
                });
    }
}
