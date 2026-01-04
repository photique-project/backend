package com.benchpress200.photique.exhibition.api.command.exception;

public class InvalidExhibitionWorkDisplayOrder extends RuntimeException {
    public static final String DEFAULT_MESSAGE = "Invalid display order";

    public InvalidExhibitionWorkDisplayOrder() {
        super(DEFAULT_MESSAGE);
    }
}
