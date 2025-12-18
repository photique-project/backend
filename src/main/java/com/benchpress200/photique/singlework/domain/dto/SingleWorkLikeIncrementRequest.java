package com.benchpress200.photique.singlework.domain.dto;

import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkLike;
import com.benchpress200.photique.user.domain.entity.User;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SingleWorkLikeIncrementRequest {
    @NotNull(message = "User's id must not be null")
    private Long userId;
    private Long singleWorkId;

    public void withSingleWorkId(
            Long singleWorkId
    ) {
        this.singleWorkId = singleWorkId;
    }

    public SingleWorkLike toEntity(
            User user,
            SingleWork singleWork
    ) {
        return SingleWorkLike.builder()
                .singleWork(singleWork)
                .user(user)
                .build();
    }
}
