package com.benchpress200.photique.singlework.application.command.service;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationUserProviderPort;
import com.benchpress200.photique.singlework.application.command.model.SingleWorkCommentCreateCommand;
import com.benchpress200.photique.singlework.application.command.port.in.CreateSingleWorkCommentUseCase;
import com.benchpress200.photique.singlework.application.command.port.out.persistence.SingleWorkCommentCommandPort;
import com.benchpress200.photique.singlework.application.query.port.out.persistence.SingleWorkQueryPort;
import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkComment;
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
        CreateSingleWorkCommentUseCase {
    private final AuthenticationUserProviderPort authenticationUserProvider;

    private final UserQueryPort userQueryPort;
    private final SingleWorkQueryPort singleWorkQueryPort;

    private final SingleWorkCommentCommandPort singleWorkCommentCommandPort;


    @Override
    public void createSingleWorkComment(SingleWorkCommentCreateCommand command) {
        // 댓글 작성 유저 조회
        Long currentUserId = authenticationUserProvider.getCurrentUserId();
        User writer = userQueryPort.findActiveById(currentUserId)
                .orElseThrow(() -> new UserNotFoundException(currentUserId));

        // 댓글 작성 단일작품 조회
        Long singleWorkId = command.getSingleWorkId();
        SingleWork singleWork = singleWorkQueryPort.findActiveById(singleWorkId)
                .orElseThrow(() -> new SingleWorkNotFoundException(singleWorkId));

        // 댓글 생성 및 저장
        SingleWorkComment singleWorkComment = command.toEntity(writer, singleWork);
        singleWorkCommentCommandPort.save(singleWorkComment);
    }
}
