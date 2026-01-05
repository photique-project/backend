package com.benchpress200.photique.singlework.domain.exception;

public class SingleWorkAlreadyLikedException extends RuntimeException {

    public SingleWorkAlreadyLikedException(
            Long userId,
            Long singleWorkId
    ) {
        super(String.format("User with id [%s] have already liked this singlework with id [%s]", userId, singleWorkId));
    }
}
