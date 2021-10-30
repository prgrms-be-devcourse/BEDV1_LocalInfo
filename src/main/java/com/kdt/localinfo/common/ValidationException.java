package com.kdt.localinfo.common;

import lombok.Getter;
import org.springframework.validation.Errors;

@Getter
public class ValidationException extends RuntimeException {

    private Errors errors;

    public ValidationException(String message, Errors errors) {
        super(message);
        this.errors = errors;
    }

    public ValidationException(String message) {
        super(message);
    }
}
