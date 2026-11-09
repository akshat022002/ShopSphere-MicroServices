package com.akshat.payment.common.exception;

public class ConcurrentUpdateException extends RuntimeException {

    public ConcurrentUpdateException(String message) {
        super(message);
    }
}
