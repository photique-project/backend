package com.benchpress200.photique.exhibition.domain.exception;

public class ExhibitionNotOwnedException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Access denied";

    public ExhibitionNotOwnedException() {
        super(DEFAULT_MESSAGE);
    }
}
