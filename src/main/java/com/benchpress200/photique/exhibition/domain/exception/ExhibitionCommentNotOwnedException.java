package com.benchpress200.photique.exhibition.domain.exception;

public class ExhibitionCommentNotOwnedException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Access denied";

    public ExhibitionCommentNotOwnedException() {
        super(DEFAULT_MESSAGE);
    }
}
