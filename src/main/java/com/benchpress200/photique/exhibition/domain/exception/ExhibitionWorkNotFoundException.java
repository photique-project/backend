package com.benchpress200.photique.exhibition.domain.exception;

public class ExhibitionWorkNotFoundException extends RuntimeException {
    public ExhibitionWorkNotFoundException(Long id) {
        super(String.format("Exhibition's work with id [%s] not found", id));
    }
}
