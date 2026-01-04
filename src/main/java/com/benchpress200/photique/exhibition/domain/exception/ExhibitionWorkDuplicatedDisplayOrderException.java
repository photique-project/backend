package com.benchpress200.photique.exhibition.domain.exception;

public class ExhibitionWorkDuplicatedDisplayOrderException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Invalid work";

    public ExhibitionWorkDuplicatedDisplayOrderException() {
        super(DEFAULT_MESSAGE);
    }
}
