package com.benchpress200.photique.exhibition.application.command.service;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationUserProviderPort;
import com.benchpress200.photique.exhibition.application.command.port.in.AddExhibitionLikeUseCase;
import com.benchpress200.photique.exhibition.application.command.port.in.CancelExhibitionLikeUseCase;
import com.benchpress200.photique.exhibition.application.command.port.out.ExhibitionCommandPort;
import com.benchpress200.photique.exhibition.application.command.port.out.ExhibitionEventPublishPort;
import com.benchpress200.photique.exhibition.application.command.port.out.ExhibitionLikeCommandPort;
import com.benchpress200.photique.exhibition.application.query.port.out.ExhibitionLikeQueryPort;
import com.benchpress200.photique.exhibition.application.query.port.out.ExhibitionQueryPort;
import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionLike;
import com.benchpress200.photique.exhibition.domain.event.ExhibitionLikeAddEvent;
import com.benchpress200.photique.exhibition.domain.exception.ExhibitionAlreadyLikedException;
import com.benchpress200.photique.exhibition.domain.exception.ExhibitionNotFoundException;
import com.benchpress200.photique.user.application.query.port.out.persistence.UserQueryPort;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExhibitionLikeCommandService implements
        AddExhibitionLikeUseCase,
        CancelExhibitionLikeUseCase {

    private final AuthenticationUserProviderPort authenticationUserProvider;

    private final UserQueryPort userQueryPort;
    private final ExhibitionQueryPort exhibitionQueryPort;
    private final ExhibitionCommandPort exhibitionCommandPort;
    private final ExhibitionLikeQueryPort exhibitionLikeQueryPort;
    private final ExhibitionLikeCommandPort exhibitionLikeCommandPort;
    private final ExhibitionEventPublishPort exhibitionEventPublishPort;

    @Override
    public void addExhibitionLike(Long exhibitionId) {
        // 요청 유저 조회
        Long currentUserId = authenticationUserProvider.getCurrentUserId();
        User user = userQueryPort.findActiveById(currentUserId)
                .orElseThrow(() -> new UserNotFoundException(currentUserId));

        // 전시회 조회
        Exhibition exhibition = exhibitionQueryPort.findActiveById(exhibitionId)
                .orElseThrow(() -> new ExhibitionNotFoundException(exhibitionId));

        // 좋아요 여부 확인
        if (exhibitionLikeQueryPort.existsByUserIdAndExhibitionId(currentUserId, exhibitionId)) {
            throw new ExhibitionAlreadyLikedException(currentUserId, exhibitionId);
        }

        // 좋아요 처리
        ExhibitionLike exhibitionLike = ExhibitionLike.of(user, exhibition);
        exhibitionLikeCommandPort.save(exhibitionLike);

        // FIXME: 좋아요 추가 & 취소 값을 언제, 어떻게 전시회 칼럼에 반영하고 ES에 동기화시킬지 전략 정해야 함
        // MySQL에 반영했을 때 업데이트 이벤트 발행?
        exhibitionCommandPort.incrementLikeCount(exhibitionId);

        // 좋아요 알림 생성 이벤트 발행
        ExhibitionLikeAddEvent event = ExhibitionLikeAddEvent.of(exhibitionId);
        exhibitionEventPublishPort.publishExhibitionLikeAddEvent(event);
    }

    @Override
    public void cancelExhibitionLike(Long exhibitionId) {
        // 요청 유저 조회
        Long currentUserId = authenticationUserProvider.getCurrentUserId();
        User user = userQueryPort.findActiveById(currentUserId)
                .orElseThrow(() -> new UserNotFoundException(currentUserId));

        // 전시회 조회
        Exhibition exhibition = exhibitionQueryPort.findActiveById(exhibitionId)
                .orElseThrow(() -> new ExhibitionNotFoundException(exhibitionId));

        // 좋아요 엔티티 조회 후 존재한다면 삭제 처리
        exhibitionLikeQueryPort.findByUserAndExhibition(user, exhibition)
                .ifPresent(exhibitionLike -> {
                    exhibitionLikeCommandPort.delete(exhibitionLike);

                    // FIXME: 좋아요 추가 & 취소 값을 언제, 어떻게 단일작품 칼럼에 반영하고 ES에 동기화시킬지 전략 정해야 함
                    exhibitionCommandPort.decrementLikeCount(exhibitionId);
                });
    }
}
