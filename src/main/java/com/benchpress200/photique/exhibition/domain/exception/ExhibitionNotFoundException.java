package com.benchpress200.photique.exhibition.domain.exception;

public class ExhibitionNotFoundException extends RuntimeException {
    public ExhibitionNotFoundException(Long id) {
        super(String.format("Exhibition with id [%s] not found", id));
    }
}
