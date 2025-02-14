package com.benchpress200.photique.exhibition.domain.dto;

import com.benchpress200.photique.common.dtovalidator.annotation.Enum;
import com.benchpress200.photique.singlework.domain.enumeration.Target;
import java.util.ArrayList;
import java.util.List;
import lombok.Setter;

@Setter
public class ExhibitionSearchRequest {
    @Enum(enumClass = Target.class, message = "Invalid value of target")
    private String target;

    private List<String> keywords;

    public Target getTarget() {
        return Target.fromValue(target);
    }

    public List<String> getKeywords() {
        if (keywords == null) {
            return new ArrayList<>();
        }

        return keywords;
    }

}
