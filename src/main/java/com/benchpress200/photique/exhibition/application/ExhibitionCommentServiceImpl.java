package com.benchpress200.photique.exhibition.application;

import com.benchpress200.photique.exhibition.domain.ExhibitionCommentDomainService;
import com.benchpress200.photique.exhibition.domain.ExhibitionDomainService;
import com.benchpress200.photique.exhibition.domain.dto.ExhibitionCommentCreateRequest;
import com.benchpress200.photique.exhibition.domain.dto.ExhibitionCommentDeleteRequest;
import com.benchpress200.photique.exhibition.domain.dto.ExhibitionCommentDetailResponse;
import com.benchpress200.photique.exhibition.domain.dto.ExhibitionCommentUpdateRequest;
import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionComment;
import com.benchpress200.photique.notification.domain.entity.Notification;
import com.benchpress200.photique.notification.domain.enumeration.NotificationType;
import com.benchpress200.photique.user.domain.UserDomainService;
import com.benchpress200.photique.user.domain.entity.User;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class ExhibitionCommentServiceImpl implements ExhibitionCommentService {

    private final UserDomainService userDomainService;
    private final ExhibitionDomainService exhibitionDomainService;
    private final ExhibitionCommentDomainService exhibitionCommentDomainService;

    @Override
    @Transactional
    public void addExhibitionComment(final ExhibitionCommentCreateRequest exhibitionCommentCreateRequest) {

        // 작성자 조회
        Long writerId = exhibitionCommentCreateRequest.getWriterId();
        User writer = userDomainService.findUser(writerId);

        // 전시회 조회
        Long exhibitionId = exhibitionCommentCreateRequest.getExhibitionId();
        Exhibition exhibition = exhibitionDomainService.findExhibition(exhibitionId);

        // 저장
        ExhibitionComment exhibitionComment = exhibitionCommentCreateRequest.toEntity(exhibition, writer);
        exhibitionCommentDomainService.createComment(exhibitionComment);

        // 알림 생성
        Long exhibitionWriterId = exhibition.getWriter().getId();
        User exhibitionWriter = userDomainService.findUser(exhibitionWriterId);

        Notification notification = Notification.builder()
                .receiver(exhibitionWriter)
                .type(NotificationType.EXHIBITION_COMMENT)
                .targetId(exhibitionId)
                .build();

//        // 알림 데이터 비동기 생성
//        notificationDomainService.createNotification(notification);
//
//        // 알림 비동기 처리
//        notificationDomainService.pushNewNotification(exhibitionWriterId);
//
//        // 업데이트 마킹
//        exhibitionDomainService.markAsUpdated(exhibition);
    }

    @Override
    public Page<ExhibitionCommentDetailResponse> getExhibitionComments(
            final Long exhibitionId,
            final Pageable pageable
    ) {
        // 전시회 조회
        Exhibition exhibition = exhibitionDomainService.findExhibition(exhibitionId);

        // 댓글 조회
        Page<ExhibitionComment> exhibitionCommentPage = exhibitionCommentDomainService.findComments(exhibition,
                pageable);

        List<ExhibitionCommentDetailResponse> exhibitionCommentDetailResponseList = exhibitionCommentPage.stream()
                .map(ExhibitionCommentDetailResponse::from)
                .toList();

        return new PageImpl<>(exhibitionCommentDetailResponseList, pageable, exhibitionCommentPage.getTotalElements());
    }

    @Override
    @Transactional
    public void updateExhibitionComment(final ExhibitionCommentUpdateRequest exhibitionCommentUpdateRequest) {
        // 작성자 조회
        Long writerId = exhibitionCommentUpdateRequest.getWriterId();
        userDomainService.findUser(writerId);

        // 전시회 조회
        Long exhibitionId = exhibitionCommentUpdateRequest.getExhibitionId();
        exhibitionDomainService.findExhibition(exhibitionId);

        // 댓글 조회
        Long commentId = exhibitionCommentUpdateRequest.getCommentId();
        ExhibitionComment exhibitionComment = exhibitionCommentDomainService.findComment(commentId);

        // 댓글 수정
        String newContent = exhibitionCommentUpdateRequest.getContent();
        exhibitionCommentDomainService.updateContent(exhibitionComment, newContent);
    }

    @Override
    @Transactional
    public void deleteExhibitionComment(final ExhibitionCommentDeleteRequest exhibitionCommentDeleteRequest) {
        // 작성자 조회
        Long writerId = exhibitionCommentDeleteRequest.getWriterId();
        userDomainService.findUser(writerId);

        // 전시회 조회
        Long exhibitionId = exhibitionCommentDeleteRequest.getExhibitionId();
        Exhibition exhibition = exhibitionDomainService.findExhibition(exhibitionId);

        // 댓글 조회
        Long commentId = exhibitionCommentDeleteRequest.getCommentId();
        ExhibitionComment exhibitionComment = exhibitionCommentDomainService.findComment(commentId);

        // 댓글 삭제
        exhibitionCommentDomainService.deleteComment(exhibitionComment);

        // 업데이트 마킹
        exhibitionDomainService.markAsUpdated(exhibition);
    }
}
