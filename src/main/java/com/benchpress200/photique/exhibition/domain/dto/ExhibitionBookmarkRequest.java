package com.benchpress200.photique.exhibition.domain.dto;

import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionBookmark;
import com.benchpress200.photique.user.domain.entity.User;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ExhibitionBookmarkRequest {

    @NotNull(message = "User's id must not be null")
    private Long userId;
    private Long exhibitionId;

    public void withExhibitionId(Long exhibitionId) {
        this.exhibitionId = exhibitionId;
    }

    public ExhibitionBookmark toEntity(
            User user,
            Exhibition exhibition
    ) {
        return ExhibitionBookmark.builder()
                .exhibition(exhibition)
                .user(user)
                .build();
    }
}
