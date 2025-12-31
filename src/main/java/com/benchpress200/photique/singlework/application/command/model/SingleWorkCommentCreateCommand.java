package com.benchpress200.photique.singlework.application.command.model;

import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkComment;
import com.benchpress200.photique.user.domain.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SingleWorkCommentCreateCommand {
    private Long singleWorkId;
    private String content;

    public SingleWorkComment toEntity(
            User writer,
            SingleWork singleWork
    ) {
        return SingleWorkComment.builder()
                .writer(writer)
                .singleWork(singleWork)
                .content(content)
                .build();
    }
}
