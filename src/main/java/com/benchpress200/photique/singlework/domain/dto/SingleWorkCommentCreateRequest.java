package com.benchpress200.photique.singlework.domain.dto;

import com.benchpress200.photique.common.dtovalidator.annotation.Id;
import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkComment;
import com.benchpress200.photique.user.domain.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class SingleWorkCommentCreateRequest {
    private Long singleWorkId;

    @NotNull(message = "Id must not be null")
    @Id
    private Long writerId;

    @NotBlank(message = "Content must not be blank.")
    @Size(max = 300, message = "Content must not exceed 300 characters")
    private String content;

    public void withSingleWorkId(
            final Long singleWorkId
    ) {
        this.singleWorkId = singleWorkId;
    }

    public SingleWorkComment toEntity(
            final User writer,
            final SingleWork singleWork
    ) {
        return SingleWorkComment.builder()
                .writer(writer)
                .singleWork(singleWork)
                .content(content)
                .build();
    }
}

