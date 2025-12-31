package com.benchpress200.photique.user.domain.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long id) {
        // %d가 아닌 %s로 toString 호출할 수 있도록하여 null 대응
        super(String.format("User with id [%s] not found", id));
    }

    public UserNotFoundException(String email) {
        super(String.format("User with email [%s] not found", email));
    }
}
