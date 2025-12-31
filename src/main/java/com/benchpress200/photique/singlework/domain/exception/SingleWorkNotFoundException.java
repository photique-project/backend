package com.benchpress200.photique.singlework.domain.exception;

public class SingleWorkNotFoundException extends RuntimeException {
    public SingleWorkNotFoundException(Long id) {
        super(String.format("SingleWork with id [%s] not found", id));
    }
}
