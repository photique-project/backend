package com.benchpress200.photique.user.application.exception;

import lombok.Getter;

@Getter
public class DuplicatedUserException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Email or nickname is already in use";

    public DuplicatedUserException() {
        super(DEFAULT_MESSAGE);
    }
}
