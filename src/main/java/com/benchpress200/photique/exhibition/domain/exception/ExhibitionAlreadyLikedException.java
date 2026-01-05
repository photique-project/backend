package com.benchpress200.photique.exhibition.domain.exception;

public class ExhibitionAlreadyLikedException extends RuntimeException {
    public ExhibitionAlreadyLikedException(
            Long userId,
            Long exhibitionId
    ) {
        super(String.format("User with id [%s] have already liked this exhibition with id [%s]", userId, exhibitionId));
    }
}
