package com.benchpress200.photique.exhibition.domain.dto;

import com.benchpress200.photique.singlework.domain.enumeration.Target;
import com.benchpress200.photique.singlework.api.validator.annotation.Enum;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
public class ExhibitionSearchRequest {
    @Enum(enumClass = Target.class, message = "Invalid value of target")
    private String target;

    private List<String> keywords;

    @Getter
    private Long userId;

    public Target getTarget() {
        return Target.from(target);
    }

    public List<String> getKeywords() {
        if (keywords == null) {
            return new ArrayList<>();
        }

        return keywords;
    }

}
