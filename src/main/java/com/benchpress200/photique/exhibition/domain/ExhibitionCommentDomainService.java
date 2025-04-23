package com.benchpress200.photique.exhibition.domain;

import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionComment;
import com.benchpress200.photique.user.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExhibitionCommentDomainService {
    void deleteComment(User user);

    void deleteComment(Exhibition exhibition);

    void createComment(ExhibitionComment exhibitionComment);

    Page<ExhibitionComment> findComments(Exhibition exhibition, Pageable pageable);

    ExhibitionComment findComment(Long commentId);

    void updateContent(ExhibitionComment exhibitionComment, String newContent);

    void deleteComment(ExhibitionComment exhibitionComment);

    long countComments(Exhibition exhibition);
}
