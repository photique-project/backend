package com.benchpress200.photique.singlework.domain.exception;

public class SingleWorkCommentNotOwnedException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Access denied";

    public SingleWorkCommentNotOwnedException() {
        super(DEFAULT_MESSAGE);
    }
}
