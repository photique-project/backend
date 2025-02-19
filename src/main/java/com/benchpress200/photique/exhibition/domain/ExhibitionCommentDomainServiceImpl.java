package com.benchpress200.photique.exhibition.domain;

import com.benchpress200.photique.common.transaction.rollbackcontext.ElasticsearchExhibitionRollbackContext;
import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionComment;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionSearch;
import com.benchpress200.photique.exhibition.exception.ExhibitionException;
import com.benchpress200.photique.exhibition.infrastructure.ExhibitionCommentRepository;
import com.benchpress200.photique.exhibition.infrastructure.ExhibitionSearchRepository;
import com.benchpress200.photique.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExhibitionCommentDomainServiceImpl implements ExhibitionCommentDomainService {

    private final ExhibitionCommentRepository exhibitionCommentRepository;
    private final ExhibitionSearchRepository exhibitionSearchRepository;

    @Override
    public void deleteComment(final User user) {
        exhibitionCommentRepository.deleteByWriter(user);
    }

    @Override
    public void deleteComment(final Exhibition exhibition) {
        exhibitionCommentRepository.deleteByExhibition(exhibition);
    }

    @Override
    public void createComment(final ExhibitionComment exhibitionComment) {
        exhibitionCommentRepository.save(exhibitionComment);

        // 엘라스틱서치 데이터 업데이트
        Exhibition exhibition = exhibitionComment.getExhibition();
        Long exhibitionId = exhibition.getId();
        Long commentCount = exhibitionCommentRepository.countByExhibition(exhibition);
        ExhibitionSearch exhibitionSearch = exhibitionSearchRepository.findById(exhibitionId).orElseThrow(
                () -> new ExhibitionException("Exhibition with id " + exhibitionId + " is not found.",
                        HttpStatus.NOT_FOUND)
        );

        exhibitionSearch.updateCommentCount(commentCount);
        ElasticsearchExhibitionRollbackContext.addDocumentToUpdate(exhibitionSearch);
    }

    @Override
    public Page<ExhibitionComment> findComments(Exhibition exhibition, Pageable pageable) {
        return exhibitionCommentRepository.findByExhibition(exhibition, pageable);
    }

    @Override
    public ExhibitionComment findComment(Long commentId) {
        return exhibitionCommentRepository.findById(commentId).orElseThrow(
                () -> new ExhibitionException("Comment with id [] is not found.", HttpStatus.NOT_FOUND)
        );
    }

    @Override
    public void updateContent(
            final ExhibitionComment exhibitionComment,
            final String newContent
    ) {
        exhibitionComment.updateContent(newContent);
    }

    @Override
    public void deleteComment(final ExhibitionComment exhibitionComment) {
        exhibitionCommentRepository.delete(exhibitionComment);

        // 엘라스틱서치 데이터 업데이트
        Exhibition exhibition = exhibitionComment.getExhibition();
        Long exhibitionId = exhibition.getId();
        Long commentCount = exhibitionCommentRepository.countByExhibition(exhibition);
        ExhibitionSearch exhibitionSearch = exhibitionSearchRepository.findById(exhibitionId).orElseThrow(
                () -> new ExhibitionException("Exhibition with id " + exhibitionId + " is not found.",
                        HttpStatus.NOT_FOUND)
        );

        exhibitionSearch.updateCommentCount(commentCount);
        ElasticsearchExhibitionRollbackContext.addDocumentToUpdate(exhibitionSearch);
    }
}
