package com.benchpress200.photique.singlework.application.command.service;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationUserProviderPort;
import com.benchpress200.photique.singlework.application.command.model.SingleWorkCommentCreateCommand;
import com.benchpress200.photique.singlework.application.command.model.SingleWorkCommentUpdateCommand;
import com.benchpress200.photique.singlework.application.command.port.in.CreateSingleWorkCommentUseCase;
import com.benchpress200.photique.singlework.application.command.port.in.DeleteSingleWorkCommentUseCase;
import com.benchpress200.photique.singlework.application.command.port.in.UpdateSingleWorkCommentUseCase;
import com.benchpress200.photique.singlework.application.command.port.out.event.SingleWorkEventPublishPort;
import com.benchpress200.photique.singlework.application.command.port.out.persistence.SingleWorkCommentCommandPort;
import com.benchpress200.photique.singlework.application.query.port.out.persistence.SingleWorkCommentQueryPort;
import com.benchpress200.photique.singlework.application.query.port.out.persistence.SingleWorkQueryPort;
import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkComment;
import com.benchpress200.photique.singlework.domain.event.SingleWorkCommentCreateEvent;
import com.benchpress200.photique.singlework.domain.exception.SingleWorkCommentNotFoundException;
import com.benchpress200.photique.singlework.domain.exception.SingleWorkCommentNotOwnedException;
import com.benchpress200.photique.singlework.domain.exception.SingleWorkNotFoundException;
import com.benchpress200.photique.user.application.query.port.out.persistence.UserQueryPort;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.exception.UserNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class SingleWorkCommentCommandService implements
        CreateSingleWorkCommentUseCase,
        UpdateSingleWorkCommentUseCase,
        DeleteSingleWorkCommentUseCase {
    private final AuthenticationUserProviderPort authenticationUserProvider;
    private final UserQueryPort userQueryPort;

    private final SingleWorkQueryPort singleWorkQueryPort;
    private final SingleWorkCommentQueryPort singleWorkCommentQueryPort;
    private final SingleWorkCommentCommandPort singleWorkCommentCommandPort;
    private final SingleWorkEventPublishPort singleWorkEventPublishPort;


    @Override
    public void createSingleWorkComment(SingleWorkCommentCreateCommand command) {
        // 댓글 작성 유저 조회
        Long currentUserId = authenticationUserProvider.getCurrentUserId();
        User writer = userQueryPort.findByIdAndDeletedAtIsNull(currentUserId)
                .orElseThrow(() -> new UserNotFoundException(currentUserId));

        // 댓글 작성 단일작품 조회
        Long singleWorkId = command.getSingleWorkId();
        SingleWork singleWork = singleWorkQueryPort.findByIdAndDeletedAtIsNull(singleWorkId)
                .orElseThrow(() -> new SingleWorkNotFoundException(singleWorkId));

        // 댓글 생성 및 저장
        SingleWorkComment singleWorkComment = command.toEntity(writer, singleWork);
        singleWorkCommentCommandPort.save(singleWorkComment);

        // 댓글 추가 알림 생성 이벤트 발행
        SingleWorkCommentCreateEvent event = SingleWorkCommentCreateEvent.of(singleWorkId);
        singleWorkEventPublishPort.publishSingleWorkCommentCreateEvent(event);
    }

    @Override
    public void updateSingleWorkComment(SingleWorkCommentUpdateCommand command) {
        // 댓글 조회
        Long singleWorkCommentId = command.getCommentId();
        SingleWorkComment singleWorkComment = singleWorkCommentQueryPort.findByIdAndDeletedAtIsNull(singleWorkCommentId)
                .orElseThrow(() -> new SingleWorkCommentNotFoundException(singleWorkCommentId));

        // 작성자 맞는지 확인
        Long writerId = authenticationUserProvider.getCurrentUserId();

        if (!singleWorkComment.isOwnedBy(writerId)) {
            throw new SingleWorkCommentNotOwnedException();
        }

        // 댓글 업데이트
        String content = command.getContent();
        singleWorkComment.updateContent(content);
    }

    @Override
    public void deleteSingleWorkComment(Long singleWorkCommentId) {
        singleWorkCommentQueryPort.findByIdAndDeletedAtIsNull(singleWorkCommentId)
                .ifPresent(singleWorkComment -> {
                    Long writerId = authenticationUserProvider.getCurrentUserId();

                    if (!singleWorkComment.isOwnedBy(writerId)) {
                        throw new SingleWorkCommentNotOwnedException();
                    }

                    singleWorkComment.delete();
                });
    }
}
