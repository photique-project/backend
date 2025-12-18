package com.benchpress200.photique.singlework.application;

import com.benchpress200.photique.notification.domain.NotificationDomainService;
import com.benchpress200.photique.notification.domain.entity.Notification;
import com.benchpress200.photique.notification.domain.enumeration.NotificationType;
import com.benchpress200.photique.singlework.domain.SingleWorkCommentDomainService;
import com.benchpress200.photique.singlework.domain.SingleWorkDomainService;
import com.benchpress200.photique.singlework.domain.dto.SingleWorkCommentCreateRequest;
import com.benchpress200.photique.singlework.domain.dto.SingleWorkCommentDeleteRequest;
import com.benchpress200.photique.singlework.domain.dto.SingleWorkCommentDetailResponse;
import com.benchpress200.photique.singlework.domain.dto.SingleWorkCommentUpdateRequest;
import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkComment;
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
public class SingleWorkCommentServiceImpl implements SingleWorkCommentService {

    private final UserDomainService userDomainService;
    private final SingleWorkDomainService singleWorkDomainService;
    private final SingleWorkCommentDomainService singleWorkCommentDomainService;
    private final NotificationDomainService notificationDomainService;

    @Override
    @Transactional
    public void addSingleWorkComment(SingleWorkCommentCreateRequest singleWorkCommentCreateRequest) {
        // 작성자 조회
        Long writerId = singleWorkCommentCreateRequest.getWriterId();
        User writer = userDomainService.findUser(writerId);

        // 작품 조회
        Long singleWorkId = singleWorkCommentCreateRequest.getSingleWorkId();
        SingleWork singleWork = singleWorkDomainService.findSingleWork(singleWorkId);

        // 단일작품 댓글 저장
        SingleWorkComment singleWorkComment = singleWorkCommentCreateRequest.toEntity(writer, singleWork);
        singleWorkCommentDomainService.addComment(singleWorkComment);

        // 알림 생성
        Long singleWorkWriterId = singleWork.getWriter().getId();
        User singleWorkWriter = userDomainService.findUser(singleWorkWriterId);

        Notification notification = Notification.builder()
                .receiver(singleWorkWriter)
                .type(NotificationType.SINGLE_WORK_COMMENT)
                .targetId(singleWorkId)
                .build();

//        // 알림 데이터 비동기 생성
//        notificationDomainService.createNotification(notification);
//
//        // 알림 비동기 처리
//        notificationDomainService.pushNewNotification(singleWorkWriterId);

        // 업데이트 마킹
        singleWorkDomainService.markAsUpdated(singleWork);
    }

    @Override
    @Transactional
    public Page<SingleWorkCommentDetailResponse> getSingleWorkComments(
            Long singleWorkId,
            Pageable pageable
    ) {
        // 단일작품 조회
        SingleWork singleWork = singleWorkDomainService.findSingleWork(singleWorkId);

        // 댓글 조회
        Page<SingleWorkComment> singleWorkComments = singleWorkCommentDomainService.findComments(singleWork, pageable);

        List<SingleWorkCommentDetailResponse> singleWorkCommentDetailResponsePage = singleWorkComments.stream()
                .map(SingleWorkCommentDetailResponse::from)
                .toList();

        return new PageImpl<>(singleWorkCommentDetailResponsePage, pageable, singleWorkComments.getTotalElements());
    }

    @Override
    @Transactional
    public void updateSingleWorkComment(SingleWorkCommentUpdateRequest singleWorkCommentUpdateRequest) {
        // 작성자 조회
        Long writerId = singleWorkCommentUpdateRequest.getWriterId();
        userDomainService.findUser(writerId);

        // 작품 조회
        Long singleWorkId = singleWorkCommentUpdateRequest.getSingleWorkId();
        singleWorkDomainService.findSingleWork(singleWorkId);

        // 댓글 조회
        Long commentId = singleWorkCommentUpdateRequest.getCommentId();
        SingleWorkComment singleWorkComment = singleWorkCommentDomainService.findComment(commentId);

        // 댓글 수정
        String newContent = singleWorkCommentUpdateRequest.getContent();
        singleWorkCommentDomainService.updateContent(singleWorkComment, newContent);
    }

    @Override
    @Transactional
    public void deleteSingleWorkComment(SingleWorkCommentDeleteRequest singleWorkCommentDeleteRequest) {
        // 작성자 조회
        Long writerId = singleWorkCommentDeleteRequest.getWriterId();
        userDomainService.findUser(writerId);

        // 작품 조회
        Long singleWorkId = singleWorkCommentDeleteRequest.getSingleWorkId();
        SingleWork singleWork = singleWorkDomainService.findSingleWork(singleWorkId);

        // 댓글 조회
        Long commentId = singleWorkCommentDeleteRequest.getCommentId();
        SingleWorkComment singleWorkComment = singleWorkCommentDomainService.findComment(commentId);

        // 댓글 삭제
        singleWorkCommentDomainService.deleteComment(singleWorkComment);

        // 업데이트 마킹
        singleWorkDomainService.markAsUpdated(singleWork);
    }
}
