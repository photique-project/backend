package com.benchpress200.photique.singlework.application.command.service;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationUserProviderPort;
import com.benchpress200.photique.outbox.application.factory.OutboxEventFactory;
import com.benchpress200.photique.outbox.application.port.out.persistence.OutboxEventPort;
import com.benchpress200.photique.outbox.domain.entity.OutboxEvent;
import com.benchpress200.photique.singlework.application.command.port.in.AddSingleWorkLikeUseCase;
import com.benchpress200.photique.singlework.application.command.port.in.CancelSingleWorkLikeUseCase;
import com.benchpress200.photique.singlework.application.command.port.out.event.SingleWorkEventPublishPort;
import com.benchpress200.photique.singlework.application.command.port.out.persistence.SingleWorkCommandPort;
import com.benchpress200.photique.singlework.application.command.port.out.persistence.SingleWorkLikeCommandPort;
import com.benchpress200.photique.singlework.application.query.port.out.persistence.SingleWorkLikeQueryPort;
import com.benchpress200.photique.singlework.application.query.port.out.persistence.SingleWorkQueryPort;
import com.benchpress200.photique.singlework.application.query.port.out.persistence.SingleWorkTagQueryPort;
import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkLike;
import com.benchpress200.photique.singlework.domain.exception.SingleWorkAlreadyLikedException;
import com.benchpress200.photique.singlework.domain.exception.SingleWorkNotFoundException;
import com.benchpress200.photique.user.application.query.port.out.persistence.UserQueryPort;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.exception.UserNotFoundException;
import java.util.List;
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
    private final SingleWorkCommandPort singleWorkCommandPort;
    private final SingleWorkLikeQueryPort singleWorkLikeQueryPort;
    private final SingleWorkLikeCommandPort singleWorkLikeCommandPort;
    private final SingleWorkEventPublishPort singleWorkEventPublishPort;
    private final SingleWorkTagQueryPort singleWorkTagQueryPort;

    private final OutboxEventFactory outboxEventFactory;
    private final OutboxEventPort outboxEventPort;

    @Override
    public void addSingleWorkLike(Long singleWorkId) {
        // 요청 유저 조회
        Long currentUserId = authenticationUserProvider.getCurrentUserId();
        User user = userQueryPort.findByIdAndDeletedAtIsNull(currentUserId)
                .orElseThrow(() -> new UserNotFoundException(currentUserId));

        // 단일작품 조회
        SingleWork singleWork = singleWorkQueryPort.findByIdAndDeletedAtIsNull(singleWorkId)
                .orElseThrow(() -> new SingleWorkNotFoundException(singleWorkId));

        // 좋아요 여부 확인
        if (singleWorkLikeQueryPort.existsByUserIdAndSingleWorkId(currentUserId, singleWorkId)) {
            throw new SingleWorkAlreadyLikedException(currentUserId, singleWorkId);
        }

        // 좋아요 처리
        SingleWorkLike singleWorkLike = SingleWorkLike.of(user, singleWork);
        singleWorkLikeCommandPort.save(singleWorkLike);
        singleWorkCommandPort.incrementLikeCount(singleWorkId);

        // 아웃박스 이벤트 발행 -> 비동기 이벤트
        // incrementLikeCount의 Modifying(flush, clear) 메서드이기 때문에 다시 영속성 컨텍스트 적재
        singleWork = singleWorkQueryPort.findByIdAndDeletedAtIsNull(singleWorkId)
                .orElseThrow(() -> new SingleWorkNotFoundException(singleWorkId));
        
        List<String> tagNames = singleWorkTagQueryPort.findBySingleWorkWithTag(singleWork).stream()
                .map(singleWorkTag -> singleWorkTag.getTag().getName())
                .toList();

        OutboxEvent outboxEvent = outboxEventFactory.singleWorkUpdated(singleWork, tagNames);
        outboxEventPort.save(outboxEvent);
    }

    @Override
    public void cancelSingleWorkLike(Long singleWorkId) {
        // 요청 유저 조회
        Long currentUserId = authenticationUserProvider.getCurrentUserId();
        User user = userQueryPort.findByIdAndDeletedAtIsNull(currentUserId)
                .orElseThrow(() -> new UserNotFoundException(currentUserId));

        // 단일작품 조회
        SingleWork singleWork = singleWorkQueryPort.findByIdAndDeletedAtIsNull(singleWorkId)
                .orElseThrow(() -> new SingleWorkNotFoundException(singleWorkId));

        // 좋아요 엔티티 조회 후 존재한다면 삭제 처리
        singleWorkLikeQueryPort.findByUserAndSingleWork(user, singleWork)
                .ifPresent(singleWorkLike -> {
                    singleWorkLikeCommandPort.delete(singleWorkLike);

                    // FIXME: 좋아요 추가 & 취소 값을 언제, 어떻게 단일작품 칼럼에 반영하고 ES에 동기화시킬지 전략 정해야 함
                    singleWorkCommandPort.decrementLikeCount(singleWorkId);
                });
    }
}
