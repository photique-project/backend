package com.benchpress200.photique.outbox.application.exception;

public class OutboxPayloadSerializationException extends RuntimeException {
    public OutboxPayloadSerializationException() {
        super("Outbox payload serialization failed");
    }
}
