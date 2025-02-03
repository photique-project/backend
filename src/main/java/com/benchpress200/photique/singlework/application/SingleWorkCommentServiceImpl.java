package com.benchpress200.photique.singlework.application;

import com.benchpress200.photique.singlework.domain.dto.SingleWorkCommentCreateRequest;
import com.benchpress200.photique.singlework.domain.dto.SingleWorkCommentDeleteRequest;
import com.benchpress200.photique.singlework.domain.dto.SingleWorkCommentDetailResponse;
import com.benchpress200.photique.singlework.domain.dto.SingleWorkCommentUpdateRequest;
import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkComment;
import com.benchpress200.photique.singlework.exception.SingleWorkException;
import com.benchpress200.photique.singlework.infrastructure.SingleWorkCommentRepository;
import com.benchpress200.photique.singlework.infrastructure.SingleWorkRepository;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.infrastructure.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class SingleWorkCommentServiceImpl implements SingleWorkCommentService {

    private final UserRepository userRepository;
    private final SingleWorkRepository singleWorkRepository;
    private final SingleWorkCommentRepository singleWorkCommentRepository;

    @Override
    public void createSingleWorkComment(final SingleWorkCommentCreateRequest singleWorkCommentCreateRequest) {

        // 작성자 조회
        final Long writerId = singleWorkCommentCreateRequest.getWriterId();
        final User writer = userRepository.findById(writerId).orElseThrow(
                () -> new SingleWorkException("User with ID " + writerId + " is not found.", HttpStatus.NOT_FOUND)
        );

        // 작품 조회
        final Long singleWorkId = singleWorkCommentCreateRequest.getSingleWorkId();
        final SingleWork singleWork = singleWorkRepository.findById(singleWorkId).orElseThrow(
                () -> new SingleWorkException("Single work with ID " + singleWorkId + " is not found.",
                        HttpStatus.NOT_FOUND)
        );

        // 저장
        final SingleWorkComment singleWorkComment = singleWorkCommentCreateRequest.toEntity(writer, singleWork);
        singleWorkCommentRepository.save(singleWorkComment);
    }

    @Override
    public Page<SingleWorkCommentDetailResponse> getSingleWorkComments(
            final Long singleWorkId,
            final Pageable pageable
    ) {
        // 단일작품조회
        singleWorkRepository.findById(singleWorkId).orElseThrow(
                () -> new SingleWorkException("Single work with ID " + singleWorkId + " is not found.",
                        HttpStatus.NOT_FOUND)
        );

        final Page<SingleWorkComment> singleWorkComments = singleWorkCommentRepository.findBySingleWorkId(
                singleWorkId, pageable
        );

        final List<SingleWorkCommentDetailResponse> singleWorkCommentDetailResponseList = singleWorkComments.stream()
                .map(SingleWorkCommentDetailResponse::from)
                .toList();

        return new PageImpl<>(singleWorkCommentDetailResponseList, pageable, singleWorkComments.getTotalElements());
    }

    @Override
    public void updateSingleWorkComment(final SingleWorkCommentUpdateRequest singleWorkCommentUpdateRequest) {
        // 작성자 조회
        final Long writerId = singleWorkCommentUpdateRequest.getWriterId();
        userRepository.findById(writerId).orElseThrow(
                () -> new SingleWorkException("User with ID " + writerId + " is not found.", HttpStatus.NOT_FOUND)
        );

        // 작품 조회
        final Long singleWorkId = singleWorkCommentUpdateRequest.getSingleWorkId();
        singleWorkRepository.findById(singleWorkId).orElseThrow(
                () -> new SingleWorkException("Single work with ID " + singleWorkId + " is not found.",
                        HttpStatus.NOT_FOUND)
        );

        // 댓글 조회
        final Long commentId = singleWorkCommentUpdateRequest.getCommentId();
        final SingleWorkComment singleWorkComment = singleWorkCommentRepository.findById(singleWorkId).orElseThrow(
                () -> new SingleWorkException("Comment in single work with ID " + commentId + " is not found.",
                        HttpStatus.NOT_FOUND)
        );

        // 댓글 수정
        singleWorkComment.updateContent(singleWorkCommentUpdateRequest.getContent());
    }

    @Override
    public void deleteSingleWorkComment(final SingleWorkCommentDeleteRequest singleWorkCommentDeleteRequest) {
        // 작성자 조회
        final Long writerId = singleWorkCommentDeleteRequest.getWriterId();
        userRepository.findById(writerId).orElseThrow(
                () -> new SingleWorkException("User with ID " + writerId + " is not found.", HttpStatus.NOT_FOUND)
        );

        // 작품 조회
        final Long singleWorkId = singleWorkCommentDeleteRequest.getSingleWorkId();
        singleWorkRepository.findById(singleWorkId).orElseThrow(
                () -> new SingleWorkException("Single work with ID " + singleWorkId + " is not found.",
                        HttpStatus.NOT_FOUND)
        );

        // 댓글 조회
        final Long commentId = singleWorkCommentDeleteRequest.getCommentId();
        singleWorkCommentRepository.findById(singleWorkId).orElseThrow(
                () -> new SingleWorkException("Comment in single work with ID " + commentId + " is not found.",
                        HttpStatus.NOT_FOUND)
        );

        // 댓글 삭제
        singleWorkCommentRepository.deleteById(commentId);
    }
}
