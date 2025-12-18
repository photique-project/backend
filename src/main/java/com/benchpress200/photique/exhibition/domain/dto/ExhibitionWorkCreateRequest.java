package com.benchpress200.photique.exhibition.domain.dto;

import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionWork;
import com.benchpress200.photique.singlework.validation.annotation.Image;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
public class ExhibitionWorkCreateRequest {
    @NotNull(message = "Image must not be null")
    @Image
    private MultipartFile image;

    @NotBlank(message = "Work title must not be blank.")
    @Size(max = 30, message = "Work title must not exceed 30 characters")
    private String title;

    @NotBlank(message = "Work description must not be blank.")
    @Size(max = 30, message = "Work description must not exceed 200 characters")
    private String description;

    public ExhibitionWork toEntity(
            Exhibition exhibition,
            String imageUrl
    ) {
        return ExhibitionWork.builder()
                .exhibition(exhibition)
                .image(imageUrl)
                .title(title)
                .description(description)
                .build();
    }
}
