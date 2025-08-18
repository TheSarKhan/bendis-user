package com.sarkhan.backend.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleDataNotFoundException(DataNotFoundException dataNotFoundException) {
        ErrorResponse errorResponse = new ErrorResponse(dataNotFoundException.getMessage(), "There isn't any data like that");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleNotEnoughQuantityException(NotEnoughQuantityException notEnoughQuantityException) {
        ErrorResponse errorResponse = new ErrorResponse(notEnoughQuantityException.getMessage(), "Not enough quantity exception");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception exception) {
        ErrorResponse errorResponse = new ErrorResponse(exception.getMessage(), "Internal server error");
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> generalExceptionHandler(Exception exception) {
        ErrorResponse errorResponse = new ErrorResponse(exception.getMessage(), "Internal Server Error");
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        StringBuilder errorMessage = new StringBuilder("Validation failed: ");
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errorMessage.append(fieldError.getField()).append(" - ").append(fieldError.getDefaultMessage()).append("; ");
        }
        ErrorResponse errorResponse = new ErrorResponse(errorMessage.toString(), "Invalid request body");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        ErrorResponse errorResponse = new ErrorResponse("Validation error: " + ex.getMessage(), "Invalid request parameter");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameter(MissingServletRequestParameterException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getParameterName(), "Parameter is required");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
