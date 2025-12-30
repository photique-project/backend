package com.benchpress200.photique.singlework.api.validator;

import com.benchpress200.photique.singlework.api.query.exception.InvalidFieldToSearch;
import java.util.Set;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

public class SortValidator {
    private static final String DELIMITER = ",";
    private static final int FIELD_INDEX = 0;
    private static final int DIRECTION_INDEX = 1;
    private static final int TOTAL_TOKEN_COUNT = 2;
    private static final String INVALID_SORT = "Invalid sort";
    private static final Set<String> ALLOWED_FIELDS =
            Set.of("createdAt", "likeCount", "viewCount");

    private static final Set<String> ALLOWED_DIRECTIONS =
            Set.of("asc", "desc");

    private static final String DEFAULT_FIELD = "createdAt";
    private static final Direction DEFAULT_DIRECTION = Direction.DESC;

    private SortValidator() {
    }

    public static Sort verifyAndParse(String sortValue) {
        // null 이거나 빈 값이라면 기본 정렬 기준으로
        if (sortValue == null || sortValue.isBlank()) {
            return Sort.by(DEFAULT_DIRECTION, DEFAULT_FIELD);
        }

        String[] parts = sortValue.split(DELIMITER);

        if (parts.length != TOTAL_TOKEN_COUNT) {
            throw new InvalidFieldToSearch(INVALID_SORT);
        }

        String field = parts[FIELD_INDEX];
        String direction = parts[DIRECTION_INDEX].toLowerCase();

        // 필드 검증
        if (!ALLOWED_FIELDS.contains(field)) {
            throw new InvalidFieldToSearch(INVALID_SORT);
        }

        // 방향 검증
        if (!ALLOWED_DIRECTIONS.contains(direction)) {
            throw new InvalidFieldToSearch(INVALID_SORT);
        }

        return Sort.by(
                Sort.Direction.fromString(direction),
                field
        );
    }
}
