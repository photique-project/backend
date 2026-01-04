package com.benchpress200.photique.exhibition.application.command.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExhibitionWorkUpdateCommand {
    private Long id;
    private Integer displayOrder;
    private String title;
    private String description;
}
