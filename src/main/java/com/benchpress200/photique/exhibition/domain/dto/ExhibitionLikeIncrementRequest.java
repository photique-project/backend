package com.benchpress200.photique.exhibition.domain.dto;

import com.benchpress200.photique.common.dtovalidator.annotation.Id;
import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionLike;
import com.benchpress200.photique.user.domain.entity.User;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ExhibitionLikeIncrementRequest {
    @NotNull(message = "User's id must not be null")
    @Id
    private Long userId;
    private Long exhibitionId;

    public void withExhibitionId(
            final Long exhibitionId
    ) {
        this.exhibitionId = exhibitionId;
    }

    public ExhibitionLike toEntity(
            final User user,
            final Exhibition exhibition
    ) {
        return ExhibitionLike.builder()
                .exhibition(exhibition)
                .user(user)
                .build();
    }
}
