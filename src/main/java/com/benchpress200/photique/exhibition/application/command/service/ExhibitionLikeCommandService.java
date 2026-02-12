package com.benchpress200.photique.exhibition.application.command.service;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationUserProviderPort;
import com.benchpress200.photique.exhibition.application.command.port.in.AddExhibitionLikeUseCase;
import com.benchpress200.photique.exhibition.application.command.port.in.CancelExhibitionLikeUseCase;
import com.benchpress200.photique.exhibition.application.command.port.out.ExhibitionCommandPort;
import com.benchpress200.photique.exhibition.application.command.port.out.ExhibitionLikeCommandPort;
import com.benchpress200.photique.exhibition.application.query.port.out.persistence.ExhibitionLikeQueryPort;
import com.benchpress200.photique.exhibition.application.query.port.out.persistence.ExhibitionQueryPort;
import com.benchpress200.photique.exhibition.application.query.port.out.persistence.ExhibitionTagQueryPort;
import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionLike;
import com.benchpress200.photique.exhibition.domain.exception.ExhibitionAlreadyLikedException;
import com.benchpress200.photique.exhibition.domain.exception.ExhibitionNotFoundException;
import com.benchpress200.photique.outbox.application.factory.OutboxEventFactory;
import com.benchpress200.photique.outbox.application.port.out.persistence.OutboxEventPort;
import com.benchpress200.photique.outbox.domain.entity.OutboxEvent;
import com.benchpress200.photique.user.application.query.port.out.persistence.UserQueryPort;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ExhibitionLikeCommandService implements
        AddExhibitionLikeUseCase,
        CancelExhibitionLikeUseCase {

    private final AuthenticationUserProviderPort authenticationUserProvider;

    private final UserQueryPort userQueryPort;
    private final ExhibitionQueryPort exhibitionQueryPort;
    private final ExhibitionCommandPort exhibitionCommandPort;
    private final ExhibitionLikeQueryPort exhibitionLikeQueryPort;
    private final ExhibitionLikeCommandPort exhibitionLikeCommandPort;
    private final ExhibitionTagQueryPort exhibitionTagQueryPort;

    private final OutboxEventFactory outboxEventFactory;
    private final OutboxEventPort outboxEventPort;

    @Override
    public void addExhibitionLike(Long exhibitionId) {
        // 요청 유저 조회
        Long currentUserId = authenticationUserProvider.getCurrentUserId();
        User user = userQueryPort.findByIdAndDeletedAtIsNull(currentUserId)
                .orElseThrow(() -> new UserNotFoundException(currentUserId));

        // 전시회 조회
        Exhibition exhibition = exhibitionQueryPort.findByIdAndDeletedAtIsNull(exhibitionId)
                .orElseThrow(() -> new ExhibitionNotFoundException(exhibitionId));

        // 좋아요 여부 확인
        if (exhibitionLikeQueryPort.existsByUserIdAndExhibitionId(currentUserId, exhibitionId)) {
            throw new ExhibitionAlreadyLikedException(currentUserId, exhibitionId);
        }

        // 좋아요 처리
        ExhibitionLike exhibitionLike = ExhibitionLike.of(user, exhibition);
        exhibitionLikeCommandPort.save(exhibitionLike);
        exhibitionCommandPort.incrementLikeCount(exhibitionId);

        // 아웃박스 이벤트 발행 -> 비동기 이벤트
        // incrementLikeCount의 Modifying(flush, clear) 메서드이기 때문에 다시 영속성 컨텍스트 적재
        exhibition = exhibitionQueryPort.findByIdAndDeletedAtIsNull(exhibitionId)
                .orElseThrow(() -> new ExhibitionNotFoundException(exhibitionId));

        OutboxEvent outboxEvent = outboxEventFactory.exhibitionLiked(exhibition);
        outboxEventPort.save(outboxEvent);
    }

    @Override
    public void cancelExhibitionLike(Long exhibitionId) {
        // 요청 유저 조회
        Long currentUserId = authenticationUserProvider.getCurrentUserId();
        User user = userQueryPort.findByIdAndDeletedAtIsNull(currentUserId)
                .orElseThrow(() -> new UserNotFoundException(currentUserId));

        // 전시회 조회
        Exhibition exhibition = exhibitionQueryPort.findByIdAndDeletedAtIsNull(exhibitionId)
                .orElseThrow(() -> new ExhibitionNotFoundException(exhibitionId));

        // 좋아요 엔티티 조회 후 존재한다면 삭제 처리
        exhibitionLikeQueryPort.findByUserAndExhibition(user, exhibition)
                .ifPresent(exhibitionLike -> {
                    exhibitionLikeCommandPort.delete(exhibitionLike);

                    exhibitionCommandPort.decrementLikeCount(exhibitionId);
                    // 아웃박스 이벤트 발행 -> 비동기 이벤트
                    // decrementLikeCount의 Modifying(flush, clear) 메서드이기 때문에 다시 영속성 컨텍스트 적재
                    Exhibition e = exhibitionQueryPort.findByIdAndDeletedAtIsNull(exhibitionId)
                            .orElseThrow(() -> new ExhibitionNotFoundException(exhibitionId));

                    OutboxEvent outboxEvent = outboxEventFactory.exhibitionUnliked(e);
                    outboxEventPort.save(outboxEvent);
                });
    }
}
