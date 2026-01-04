package com.benchpress200.photique.exhibition.api.command.request;

import com.benchpress200.photique.exhibition.api.command.exception.InvalidExhibitionFieldToUpdateException;
import com.benchpress200.photique.exhibition.application.command.model.ExhibitionUpdateCommand;
import com.benchpress200.photique.exhibition.application.command.model.ExhibitionWorkUpdateCommand;
import com.benchpress200.photique.tag.api.validator.annotation.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ExhibitionUpdateRequest {
    private static final String INVALID_TITLE = "Invalid title";
    private static final String INVALID_DESCRIPTION = "Invalid description";
    private static final String INVALID_CARD_COLOR = "Invalid card color";
    private static final String INVALID_WORKS = "Invalid works";

    @Size(min = 1, max = 30, message = INVALID_TITLE)
    private String title;
    private boolean updateTitle;

    @Size(min = 1, max = 500, message = INVALID_DESCRIPTION)
    private String description;
    private boolean updateDescription;

    @Size(max = 20, message = INVALID_CARD_COLOR)
    private String cardColor;
    private boolean updateCardColor;

    @Tag
    private List<String> tags;
    private boolean updateTags;

    @Valid
    @Size(min = 1, max = 10, message = INVALID_WORKS)
    private List<ExhibitionWorkUpdateRequest> works;
    private boolean updateWorks;

    public ExhibitionUpdateCommand toCommand(Long exhibitionId) {
        if (updateTitle && title == null) {
            throw new InvalidExhibitionFieldToUpdateException(INVALID_TITLE);
        }

        if (updateDescription && description == null) {
            throw new InvalidExhibitionFieldToUpdateException(INVALID_DESCRIPTION);
        }

        if (updateCardColor && cardColor == null) {
            throw new InvalidExhibitionFieldToUpdateException(INVALID_CARD_COLOR);
        }

        // 태그를 빈 값으로 업데이트할 경우 NPE 방지를 위한 빈 리스트 할당
        if (updateTags && tags == null) {
            tags = new ArrayList<>();
        }

        // 개별 작품 업데이트 플래그가 유효한대 대상이 null일 경우
        if (updateWorks && works == null) {
            throw new InvalidExhibitionFieldToUpdateException(INVALID_WORKS);
        }

        // NPE 방지를 위한 빈 리스트 할당
        if (!updateWorks) {
            works = new ArrayList<>();
        }

        // 검색데이터(ES 저장) 업데이트 플래그
        boolean update = updateTitle ||
                updateDescription ||
                updateTags;

        List<ExhibitionWorkUpdateCommand> workCommands = works.stream()
                .map(request -> ExhibitionWorkUpdateCommand.builder()
                        .id(request.getId())
                        .displayOrder(request.getDisplayOrder())
                        .title(request.getTitle())
                        .description(request.getDescription())
                        .build())
                .toList();

        return ExhibitionUpdateCommand.builder()
                .exhibitionId(exhibitionId)
                .updateTitle(updateTitle)
                .title(title)
                .updateDescription(updateDescription)
                .description(description)
                .updateCardColor(updateCardColor)
                .cardColor(cardColor)
                .updateTags(updateTags)
                .tags(tags)
                .updateWorks(updateWorks)
                .works(workCommands)
                .update(update)
                .build();
    }
}
