package com.benchpress200.photique.singlework.application.query.result;

import com.benchpress200.photique.singlework.domain.entity.SingleWorkComment;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@Builder
public class SingleWorkCommentsResult {
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean isFirst;
    private boolean isLast;
    private boolean hasNext;
    private boolean hasPrevious;
    private List<SingleWorkCommentView> comments;

    public static SingleWorkCommentsResult from(
            Page<SingleWorkComment> singleWorkCommentPage
    ) {
        List<SingleWorkCommentView> comments = singleWorkCommentPage.getContent()
                .stream()
                .map(SingleWorkCommentView::from)
                .toList();

        return SingleWorkCommentsResult.builder()
                .page(singleWorkCommentPage.getNumber())
                .size(singleWorkCommentPage.getSize())
                .totalElements(singleWorkCommentPage.getTotalElements())
                .totalPages(singleWorkCommentPage.getTotalPages())
                .isFirst(singleWorkCommentPage.isFirst())
                .isLast(singleWorkCommentPage.isLast())
                .hasNext(singleWorkCommentPage.hasNext())
                .hasPrevious(singleWorkCommentPage.hasPrevious())
                .comments(comments)
                .build();
    }
}
