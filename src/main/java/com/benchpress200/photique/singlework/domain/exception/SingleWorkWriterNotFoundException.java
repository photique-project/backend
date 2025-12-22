package com.benchpress200.photique.singlework.domain.exception;

public class SingleWorkWriterNotFoundException extends RuntimeException {
    public SingleWorkWriterNotFoundException(Long writerId) {
        super("Writer with id [" + writerId + "] not found");
    }
}
