package com.benchpress200.photique.singlework.domain.exception;

public class SingleWorkAlreadyLikedException extends RuntimeException {

    public SingleWorkAlreadyLikedException(
            Long userId,
            Long singleWorkId
    ) {
        super("User with id [" + userId + "] have already liked this singleWork with id [" + singleWorkId + "]");
    }
}
