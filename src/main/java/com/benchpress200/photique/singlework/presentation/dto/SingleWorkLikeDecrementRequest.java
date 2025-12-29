package com.benchpress200.photique.singlework.presentation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SingleWorkLikeDecrementRequest {
    @NotNull(message = "User's id must not be null")
    private Long userId;
    private Long singleWorkId;

    public void withSingleWorkId(Long singleWorkId) {
        this.singleWorkId = singleWorkId;
    }
}
