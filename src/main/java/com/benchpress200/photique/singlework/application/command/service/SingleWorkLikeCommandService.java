package com.benchpress200.photique.singlework.application.command.service;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationUserProviderPort;
import com.benchpress200.photique.singlework.application.command.port.in.AddSingleWorkLikeUseCase;
import com.benchpress200.photique.singlework.application.command.port.in.CancelSingleWorkLikeUseCase;
import com.benchpress200.photique.singlework.application.command.port.out.persistence.SingleWorkLikeCommandPort;
import com.benchpress200.photique.singlework.application.query.port.out.persistence.SingleWorkLikeQueryPort;
import com.benchpress200.photique.singlework.application.query.port.out.persistence.SingleWorkQueryPort;
import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkLike;
import com.benchpress200.photique.singlework.domain.exception.SingleWorkAlreadyLikedException;
import com.benchpress200.photique.singlework.domain.exception.SingleWorkNotFoundException;
import com.benchpress200.photique.user.application.query.port.out.persistence.UserQueryPort;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SingleWorkLikeCommandService implements
        AddSingleWorkLikeUseCase,
        CancelSingleWorkLikeUseCase {

    private final AuthenticationUserProviderPort authenticationUserProvider;

    private final UserQueryPort userQueryPort;
    private final SingleWorkQueryPort singleWorkQueryPort;
    private final SingleWorkLikeQueryPort singleWorkLikeQueryPort;
    private final SingleWorkLikeCommandPort singleWorkLikeCommandPort;

    @Override
    public void addSingleWorkLike(Long singleWorkId) {
        // 요청 유저 아이디 꺼내기
        Long currentUserId = authenticationUserProvider.getCurrentUserId();
        User user = userQueryPort.findActiveById(currentUserId)
                .orElseThrow(() -> new UserNotFoundException(currentUserId));

        // 단일작품 조회
        SingleWork singleWork = singleWorkQueryPort.findActiveById(singleWorkId)
                .orElseThrow(() -> new SingleWorkNotFoundException(singleWorkId));

        // 좋아요 여부 확인
        if (singleWorkLikeQueryPort.existsByUserIdAndSingleWorkId(currentUserId, singleWorkId)) {
            throw new SingleWorkAlreadyLikedException(currentUserId, singleWorkId);
        }

        // 좋아요 처리
        SingleWorkLike singleWorkLike = SingleWorkLike.of(user, singleWork);
        singleWorkLikeCommandPort.save(singleWorkLike);
    }

    @Override
    public void cancelSingleWorkLike(Long singleWorkId) {
        // 요청 유저 아이디 꺼내기
        Long currentUserId = authenticationUserProvider.getCurrentUserId();
        User user = userQueryPort.findActiveById(currentUserId)
                .orElseThrow(() -> new UserNotFoundException(currentUserId));

        // 단일작품 조회
        SingleWork singleWork = singleWorkQueryPort.findActiveById(singleWorkId)
                .orElseThrow(() -> new SingleWorkNotFoundException(singleWorkId));

        // 좋아요 엔티티 조회 후 존재한다면 삭제 처리
        singleWorkLikeQueryPort.findByUserAndSingleWork(user, singleWork)
                .ifPresent(singleWorkLikeCommandPort::delete);
    }
}
