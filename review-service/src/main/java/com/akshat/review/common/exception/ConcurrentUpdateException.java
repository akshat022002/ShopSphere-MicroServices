package com.akshat.review.common.exception;

public class ConcurrentUpdateException extends RuntimeException {

    public ConcurrentUpdateException(String message) {
        super(message);
    }
}
