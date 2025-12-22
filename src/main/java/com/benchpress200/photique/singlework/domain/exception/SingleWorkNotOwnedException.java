package com.benchpress200.photique.singlework.domain.exception;

public class SingleWorkNotOwnedException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Access denied";

    public SingleWorkNotOwnedException() {
        super(DEFAULT_MESSAGE);
    }
}
