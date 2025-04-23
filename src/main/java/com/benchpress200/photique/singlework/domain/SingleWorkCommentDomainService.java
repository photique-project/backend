package com.benchpress200.photique.singlework.domain;

import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkComment;
import com.benchpress200.photique.user.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SingleWorkCommentDomainService {
    void deleteComment(User writer);

    void deleteComment(SingleWork singleWork);

    void addComment(SingleWorkComment singleWorkComment);

    Page<SingleWorkComment> findComments(SingleWork singleWork, Pageable pageable);

    SingleWorkComment findComment(Long commentId);

    void updateContent(SingleWorkComment singleWorkComment, String newContent);

    void deleteComment(SingleWorkComment singleWorkComment);

    long countComments(SingleWork singleWork);
}
