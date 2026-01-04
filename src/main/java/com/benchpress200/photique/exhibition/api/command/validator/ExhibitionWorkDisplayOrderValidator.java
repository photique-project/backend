package com.benchpress200.photique.exhibition.api.command.validator;

import com.benchpress200.photique.exhibition.api.command.request.ExhibitionWorkCreateRequest;
import java.util.List;

public class ExhibitionWorkDisplayOrderValidator {
    private ExhibitionWorkDisplayOrderValidator() {
    }

    public static boolean isValid(List<ExhibitionWorkCreateRequest> requests) {
        return requests.stream()
                .map(ExhibitionWorkCreateRequest::getDisplayOrder)
                .distinct()
                .count() == requests.size();
    }
}
