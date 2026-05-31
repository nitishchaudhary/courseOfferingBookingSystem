package com.project.classOfferingBookingSystem.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApplicationException extends RuntimeException{

    private final HttpStatus statusCode;

    public ApplicationException(HttpStatus statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }
}
