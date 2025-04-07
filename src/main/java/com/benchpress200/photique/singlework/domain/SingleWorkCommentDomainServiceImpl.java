package com.benchpress200.photique.singlework.domain;

import com.benchpress200.photique.common.transaction.rollbackcontext.ElasticsearchSingleWorkRollbackContext;
import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkComment;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkSearch;
import com.benchpress200.photique.singlework.exception.SingleWorkException;
import com.benchpress200.photique.singlework.infrastructure.SingleWorkCommentRepository;
import com.benchpress200.photique.singlework.infrastructure.SingleWorkSearchRepository;
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

        // 엘라스틱서치 데이터 업데이트
        SingleWork singleWork = singleWorkComment.getSingleWork();
        Long singleWorkId = singleWork.getId();
        Long commentCount = singleWorkCommentRepository.countBySingleWork(singleWork);
        SingleWorkSearch singleWorkSearch = singleWorkSearchRepository.findById(singleWorkId).orElseThrow(
                () -> new SingleWorkException("Single work with id " + singleWorkId + " is not found.",
                        HttpStatus.NOT_FOUND)
        );

        singleWorkSearch.updateCommentCount(commentCount);
        ElasticsearchSingleWorkRollbackContext.addDocumentToUpdate(singleWorkSearch);
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

        // 엘라스틱서치 업데이트
        SingleWork singleWork = singleWorkComment.getSingleWork();
        Long singleWorkId = singleWork.getId();
        Long commentCount = singleWorkCommentRepository.countBySingleWork(singleWork);
        SingleWorkSearch singleWorkSearch = singleWorkSearchRepository.findById(singleWorkId).orElseThrow(
                () -> new SingleWorkException("Single work with id " + singleWorkId + " is not found.",
                        HttpStatus.NOT_FOUND)
        );

        singleWorkSearch.updateCommentCount(commentCount);
        ElasticsearchSingleWorkRollbackContext.addDocumentToUpdate(singleWorkSearch);
    }
}
