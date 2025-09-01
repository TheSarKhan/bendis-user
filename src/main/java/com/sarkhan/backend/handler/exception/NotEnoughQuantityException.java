package com.sarkhan.backend.handler.exception;

public class NotEnoughQuantityException extends Exception{
    public NotEnoughQuantityException(String message) {
        super(message);
    }
}
