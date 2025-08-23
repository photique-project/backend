package com.benchpress200.photique.singlework.domain;

import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkComment;
import com.benchpress200.photique.singlework.exception.SingleWorkException;
import com.benchpress200.photique.singlework.domain.repository.SingleWorkCommentRepository;
import com.benchpress200.photique.singlework.domain.repository.SingleWorkSearchRepository;
import com.benchpress200.photique.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SingleWorkCommentDomainServiceImpl implements SingleWorkCommentDomainService {

    private final SingleWorkCommentRepository singleWorkCommentRepository;
    private final SingleWorkSearchRepository singleWorkSearchRepository;

    @Override
    public void deleteComment(final User writer) {
        singleWorkCommentRepository.deleteByWriter(writer);
    }

    @Override
    public void deleteComment(SingleWork singleWork) {
        singleWorkCommentRepository.deleteBySingleWork(singleWork);
    }

    @Override
    public void addComment(final SingleWorkComment singleWorkComment) {
        singleWorkCommentRepository.save(singleWorkComment);
    }

    @Override
    public Page<SingleWorkComment> findComments(final SingleWork singleWork, final Pageable pageable) {
        Page<SingleWorkComment> singleWorkCommentPage = singleWorkCommentRepository.findBySingleWork(singleWork,
                pageable);
        if (singleWorkCommentPage.getTotalElements() == 0) {
            throw new SingleWorkException("Comments in the single work is not found.", HttpStatus.NOT_FOUND);
        }

        return singleWorkCommentPage;
    }

    @Override
    public SingleWorkComment findComment(Long commentId) {
        return singleWorkCommentRepository.findById(commentId).orElseThrow(
                () -> new SingleWorkException("Comment [" + commentId + "] is not found.", HttpStatus.NOT_FOUND)
        );
    }

    @Override
    public void updateContent(
            final SingleWorkComment singleWorkComment,
            final String newContent
    ) {
        singleWorkComment.updateContent(newContent);
    }

    @Override
    public void deleteComment(final SingleWorkComment singleWorkComment) {
        singleWorkCommentRepository.delete(singleWorkComment);
    }

    @Override
    public long countComments(final SingleWork singleWork) {
        return singleWorkCommentRepository.countBySingleWork(singleWork);
    }
}
