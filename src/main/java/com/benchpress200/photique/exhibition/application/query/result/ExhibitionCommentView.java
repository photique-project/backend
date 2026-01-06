package com.benchpress200.photique.exhibition.application.query.result;

import com.benchpress200.photique.exhibition.domain.entity.ExhibitionComment;
import com.benchpress200.photique.user.domain.entity.User;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExhibitionCommentView {
    private Long id;
    private Writer writer;
    private String content;
    private LocalDateTime createdAt;

    public static ExhibitionCommentView from(ExhibitionComment exhibitionComment) {
        User user = exhibitionComment.getWriter();
        Long writerId = user.getId();
        String writerNickname = user.getNickname();
        String writerProfileImage = user.getProfileImage();

        Writer writer = Writer.of(writerId, writerNickname, writerProfileImage);

        return ExhibitionCommentView.builder()
                .id(exhibitionComment.getId())
                .writer(writer)
                .content(exhibitionComment.getContent())
                .createdAt(exhibitionComment.getCreatedAt())
                .build();
    }
}
