package com.benchpress200.photique.singlework.domain.exception;

public class SingleWorkNotFoundException extends RuntimeException {
    public SingleWorkNotFoundException(Long id) {
        super("SingleWork with id [" + id + "+] not found");
    }
}
