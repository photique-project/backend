package com.benchpress200.photique.exhibition.application.command.model;

import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionWork;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
public class ExhibitionWorkCreateCommand {
    private Integer displayOrder;
    private String title;
    private String description;
    private MultipartFile image;

    public ExhibitionWork toEntity(
            Exhibition exhibition,
            String imageUrl) {
        return ExhibitionWork.builder()
                .exhibition(exhibition)
                .displayOrder(displayOrder)
                .title(title)
                .description(description)
                .image(imageUrl)
                .build();
    }
}
