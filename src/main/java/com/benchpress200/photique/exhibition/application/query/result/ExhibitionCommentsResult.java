package com.benchpress200.photique.exhibition.application.query.result;

import com.benchpress200.photique.exhibition.domain.entity.ExhibitionComment;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@Builder
public class ExhibitionCommentsResult {
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean isFirst;
    private boolean isLast;
    private boolean hasNext;
    private boolean hasPrevious;
    private List<ExhibitionCommentView> comments;

    public static ExhibitionCommentsResult from(
            Page<ExhibitionComment> exhibitionCommentPage
    ) {
        List<ExhibitionCommentView> comments = exhibitionCommentPage.stream()
                .map(ExhibitionCommentView::from)
                .toList();

        return ExhibitionCommentsResult.builder()
                .page(exhibitionCommentPage.getNumber())
                .size(exhibitionCommentPage.getSize())
                .totalElements(exhibitionCommentPage.getTotalElements())
                .totalPages(exhibitionCommentPage.getTotalPages())
                .isFirst(exhibitionCommentPage.isFirst())
                .isLast(exhibitionCommentPage.isLast())
                .hasNext(exhibitionCommentPage.hasNext())
                .hasPrevious(exhibitionCommentPage.hasPrevious())
                .comments(comments)
                .build();
    }
}
