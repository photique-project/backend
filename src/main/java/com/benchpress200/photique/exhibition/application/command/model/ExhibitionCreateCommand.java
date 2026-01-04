package com.benchpress200.photique.exhibition.application.command.model;

import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.user.domain.entity.User;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExhibitionCreateCommand {
    private String title;
    private String description;
    private String cardColor;
    private List<String> tags;
    private List<ExhibitionWorkCreateCommand> works;

    public Exhibition toEntity(User writer) {
        return Exhibition.builder()
                .writer(writer)
                .title(title)
                .description(description)
                .cardColor(cardColor)
                .build();
    }
}
