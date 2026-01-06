package com.benchpress200.photique.exhibition.domain.exception;

public class ExhibitionCommentNotFoundException extends RuntimeException {
    public ExhibitionCommentNotFoundException(Long exhibitionCommentId) {
        super(String.format("Exhibition's comment with id [%s] not found", exhibitionCommentId));
    }
}
