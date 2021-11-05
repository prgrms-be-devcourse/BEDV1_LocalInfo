package com.kdt.localinfo.error;

import lombok.Getter;
import org.springframework.validation.Errors;

@Getter
public class InvalidInputException extends RuntimeException {

    private Errors errors;

    public InvalidInputException(String message, Errors errors) {
        super(message);
        this.errors = errors;
    }

    public InvalidInputException(String message) {
        super(message);
    }
}
