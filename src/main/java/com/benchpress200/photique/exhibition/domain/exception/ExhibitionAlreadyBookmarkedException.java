package com.benchpress200.photique.exhibition.domain.exception;

public class ExhibitionAlreadyBookmarkedException extends RuntimeException {
    public ExhibitionAlreadyBookmarkedException(
            Long userId,
            Long exhibitionId
    ) {
        super(String.format("User with id [%s] have already bookmarked this exhibition with id [%s]", userId,
                exhibitionId));
    }
}
