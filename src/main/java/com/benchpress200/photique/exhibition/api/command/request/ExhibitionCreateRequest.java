package com.benchpress200.photique.exhibition.api.command.request;

import com.benchpress200.photique.exhibition.api.command.exception.InvalidExhibitionImage;
import com.benchpress200.photique.exhibition.api.command.exception.InvalidExhibitionWorkDisplayOrder;
import com.benchpress200.photique.exhibition.api.command.validator.ExhibitionWorkDisplayOrderValidator;
import com.benchpress200.photique.exhibition.application.command.model.ExhibitionCreateCommand;
import com.benchpress200.photique.exhibition.application.command.model.ExhibitionWorkCreateCommand;
import com.benchpress200.photique.image.presentation.validator.ImageValidator;
import com.benchpress200.photique.tag.api.validator.annotation.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
public class ExhibitionCreateRequest {
    private static final String INVALID_TITLE = "Invalid title";
    private static final String INVALID_DESCRIPTION = "Invalid description";
    private static final String INVALID_CARD_COLOR = "Invalid card color";
    private static final String INVALID_WORKS = "Invalid works";

    @NotBlank(message = INVALID_TITLE)
    @Size(min = 1, max = 30, message = INVALID_TITLE)
    private String title;

    @NotBlank(message = INVALID_DESCRIPTION)
    @Size(min = 1, max = 200, message = INVALID_DESCRIPTION)
    private String description;

    @NotBlank(message = INVALID_CARD_COLOR)
    @Size(max = 20, message = INVALID_CARD_COLOR)
    private String cardColor;

    @Tag
    private List<String> tags;

    @Valid
    @NotNull(message = INVALID_WORKS)
    @Size(min = 1, max = 10, message = INVALID_WORKS)
    private List<ExhibitionWorkCreateRequest> works;

    public ExhibitionCreateCommand toCommand(List<MultipartFile> images) {
        if (!images.stream().allMatch(ImageValidator::isValid)) {
            throw new InvalidExhibitionImage();
        }

        // null 값이라면 NPE 방지를 위한 빈 리스트 할당
        if (tags == null) {
            tags = new ArrayList<>();
        }

        // 중복 displayOrder가 없는지 검사
        if (!ExhibitionWorkDisplayOrderValidator.isValid(works)) {
            throw new InvalidExhibitionWorkDisplayOrder();
        }

        List<ExhibitionWorkCreateCommand> workCommands = works.stream()
                .map(request ->
                        ExhibitionWorkCreateCommand.builder()
                                .displayOrder(request.getDisplayOrder())
                                .title(request.getTitle())
                                .description(request.getDescription())
                                .image(images.get(request.getDisplayOrder()))
                                .build()
                ).toList();

        return ExhibitionCreateCommand.builder()
                .title(title)
                .description(description)
                .cardColor(cardColor)
                .tags(tags)
                .works(workCommands)
                .build();
    }
}
