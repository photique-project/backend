package com.benchpress200.photique.singlework.infrastructure.exception;

public class ElasticsearchSearchException extends RuntimeException {
    public ElasticsearchSearchException(String message) {
        super(message);
    }
}
