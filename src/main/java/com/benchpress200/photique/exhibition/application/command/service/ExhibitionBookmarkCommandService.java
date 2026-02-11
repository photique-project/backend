package com.benchpress200.photique.exhibition.application.command.service;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationUserProviderPort;
import com.benchpress200.photique.exhibition.application.command.port.in.AddExhibitionBookmarkUseCase;
import com.benchpress200.photique.exhibition.application.command.port.in.CancelExhibitionBookmarkUseCase;
import com.benchpress200.photique.exhibition.application.command.port.out.ExhibitionBookmarkCommandPort;
import com.benchpress200.photique.exhibition.application.query.port.out.persistence.ExhibitionBookmarkQueryPort;
import com.benchpress200.photique.exhibition.application.query.port.out.persistence.ExhibitionQueryPort;
import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionBookmark;
import com.benchpress200.photique.exhibition.domain.exception.ExhibitionAlreadyBookmarkedException;
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
public class ExhibitionBookmarkCommandService implements
        AddExhibitionBookmarkUseCase,
        CancelExhibitionBookmarkUseCase {

    private final AuthenticationUserProviderPort authenticationUserProvider;

    private final UserQueryPort userQueryPort;
    private final ExhibitionQueryPort exhibitionQueryPort;
    private final ExhibitionBookmarkQueryPort exhibitionBookmarkQueryPort;
    private final ExhibitionBookmarkCommandPort exhibitionBookmarkCommandPort;

    @Override
    public void addExhibitionBookmark(Long exhibitionId) {
        // 요청 유저 조회
        Long currentUserId = authenticationUserProvider.getCurrentUserId();
        User user = userQueryPort.findByIdAndDeletedAtIsNull(currentUserId)
                .orElseThrow(() -> new UserNotFoundException(currentUserId));

        // 전시회 조회
        Exhibition exhibition = exhibitionQueryPort.findByIdAndDeletedAtIsNull(exhibitionId)
                .orElseThrow(() -> new ExhibitionNotFoundException(exhibitionId));

        // 북마크 여부 확인
        if (exhibitionBookmarkQueryPort.existsByUserIdAndExhibitionId(currentUserId, exhibitionId)) {
            throw new ExhibitionAlreadyBookmarkedException(currentUserId, exhibitionId);
        }

        // 북마크 처리
        ExhibitionBookmark exhibitionBookmark = ExhibitionBookmark.of(user, exhibition);
        exhibitionBookmarkCommandPort.save(exhibitionBookmark);
    }

    @Override
    public void cancelExhibitionBookmark(Long exhibitionId) {
        // 요청 유저 조회
        Long currentUserId = authenticationUserProvider.getCurrentUserId();
        User user = userQueryPort.findByIdAndDeletedAtIsNull(currentUserId)
                .orElseThrow(() -> new UserNotFoundException(currentUserId));

        // 전시회 조회
        Exhibition exhibition = exhibitionQueryPort.findByIdAndDeletedAtIsNull(exhibitionId)
                .orElseThrow(() -> new ExhibitionNotFoundException(exhibitionId));

        // 북마크 엔티티 조회 후 존재한다면 삭제 처리
        exhibitionBookmarkQueryPort.findByUserAndExhibition(user, exhibition)
                .ifPresent(exhibitionBookmarkCommandPort::delete);
    }
}
