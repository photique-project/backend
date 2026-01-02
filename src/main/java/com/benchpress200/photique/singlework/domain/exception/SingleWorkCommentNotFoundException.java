package com.benchpress200.photique.singlework.domain.exception;

public class SingleWorkCommentNotFoundException extends RuntimeException {
    public SingleWorkCommentNotFoundException(Long id) {
        super(String.format("SingleWork's comment with id [%s] not found", id));
    }
}
