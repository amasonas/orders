package com.example.order.application.exceptions;

public class IdempotencyViolationException extends RuntimeException {

    public IdempotencyViolationException(String idempotencyId) {
        super(String.format("Register with idempotencyId %s already exists", idempotencyId));
    }
}