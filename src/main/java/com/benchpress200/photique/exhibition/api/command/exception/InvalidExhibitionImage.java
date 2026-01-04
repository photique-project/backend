package com.benchpress200.photique.exhibition.api.command.exception;

public class InvalidExhibitionImage extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Invalid image";

    public InvalidExhibitionImage() {
        super(DEFAULT_MESSAGE);
    }
}
