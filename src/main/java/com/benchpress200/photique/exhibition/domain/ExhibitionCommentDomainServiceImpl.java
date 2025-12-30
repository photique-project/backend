package com.benchpress200.photique.exhibition.domain;

import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionComment;
import com.benchpress200.photique.exhibition.exception.ExhibitionException;
import com.benchpress200.photique.exhibition.infrastructure.persistence.jpa.ExhibitionCommentRepository;
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

    @Override
    public void deleteComment(User user) {
        exhibitionCommentRepository.deleteByWriter(user);
    }

    @Override
    public void deleteComment(Exhibition exhibition) {
        exhibitionCommentRepository.deleteByExhibition(exhibition);
    }

    @Override
    public void createComment(ExhibitionComment exhibitionComment) {
        exhibitionCommentRepository.save(exhibitionComment);
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
            ExhibitionComment exhibitionComment,
            String newContent
    ) {
        exhibitionComment.updateContent(newContent);
    }

    @Override
    public void deleteComment(ExhibitionComment exhibitionComment) {
        exhibitionCommentRepository.delete(exhibitionComment);
    }

    @Override
    public long countComments(Exhibition exhibition) {
        return exhibitionCommentRepository.countByExhibition(exhibition);
    }
}
