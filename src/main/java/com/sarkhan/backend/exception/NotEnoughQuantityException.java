package com.sarkhan.backend.exception;

public class NotEnoughQuantityException extends Exception{
    public NotEnoughQuantityException(String message) {
        super(message);
    }
}
