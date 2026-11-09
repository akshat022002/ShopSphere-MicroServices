package com.akshat.ai.common.exception;

public class ConcurrentUpdateException extends RuntimeException {

    public ConcurrentUpdateException(String message) {
        super(message);
    }
}
