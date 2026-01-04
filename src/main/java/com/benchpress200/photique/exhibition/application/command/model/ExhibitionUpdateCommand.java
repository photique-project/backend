package com.benchpress200.photique.exhibition.application.command.model;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExhibitionUpdateCommand {
    private Long exhibitionId;

    private boolean updateTitle;
    private String title;

    private boolean updateDescription;
    private String description;

    private boolean updateCardColor;
    private String cardColor;

    private boolean updateTags;
    private List<String> tags;

    private boolean updateWorks;
    private List<ExhibitionWorkUpdateCommand> works;

    private boolean update;
}
