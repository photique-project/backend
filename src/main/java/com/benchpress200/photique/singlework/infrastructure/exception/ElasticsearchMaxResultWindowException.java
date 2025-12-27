package com.benchpress200.photique.singlework.infrastructure.exception;

public class ElasticsearchMaxResultWindowException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Elasticsearch max result window exceeded";

    public ElasticsearchMaxResultWindowException() {
        super(DEFAULT_MESSAGE);
    }
}
