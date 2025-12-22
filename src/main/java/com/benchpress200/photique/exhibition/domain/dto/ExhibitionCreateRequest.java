package com.benchpress200.photique.exhibition.domain.dto;

import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionTag;
import com.benchpress200.photique.tag.domain.entity.Tag;
import com.benchpress200.photique.user.domain.entity.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ExhibitionCreateRequest {
    @NotBlank(message = "Title must not be blank.")
    @Size(max = 30, message = "Title must not exceed 30 characters")
    private String title;

    @NotNull(message = "Writer id must not be null")
    private Long writerId;

    @NotBlank(message = "Description must not be blank.")
    @Size(max = 30, message = "Description must not exceed 200 characters")
    private String description;

    @Size(max = 5, message = "Tag list size must be between 0 and 5")
    @Valid
//    private List<NewTagRequest> tags;

    @NotBlank(message = "Card color must not be blank")
    @Size(max = 30)
    private String cardColor;

    @Size(min = 1, max = 10, message = "Work list size must be between 1 and 10")
    @Valid
    private List<ExhibitionWorkCreateRequest> works;

//    public boolean hasTags() {
//        return tags != null && !tags.isEmpty();
//    }

    public Exhibition toEntity(
            User writer
    ) {
        return Exhibition.builder()
                .writer(writer)
                .title(title)
                .description(description)
                .cardColor(cardColor)
                .build();
    }

    public List<ExhibitionTag> toExhibitionTagEntities(
            Exhibition exhibition,
            List<Tag> tags
    ) {
        return tags.stream()
                .map(tag -> ExhibitionTag.builder()
                        .exhibition(exhibition)
                        .tag(tag)
                        .build()
                )
                .toList();
    }

//    public List<String> getTags() {
//        return tags.stream().map(NewTagRequest::getName).toList();
//    }
}
