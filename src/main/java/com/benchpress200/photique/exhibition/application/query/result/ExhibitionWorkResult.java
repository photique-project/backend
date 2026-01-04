package com.benchpress200.photique.exhibition.application.query.result;

import com.benchpress200.photique.exhibition.domain.entity.ExhibitionWork;
import lombok.Getter;

@Getter
public class ExhibitionWorkResult {
    private final Long id;
    private final Integer displayOrder;
    private final String title;
    private final String description;
    private final String image;

    private ExhibitionWorkResult(ExhibitionWork exhibitionWork) {
        this.id = exhibitionWork.getId();
        this.displayOrder = exhibitionWork.getDisplayOrder();
        this.title = exhibitionWork.getTitle();
        this.description = exhibitionWork.getDescription();
        this.image = exhibitionWork.getImage();
    }

    public static ExhibitionWorkResult from(ExhibitionWork exhibitionWork) {
        return new ExhibitionWorkResult(exhibitionWork);
    }
}
