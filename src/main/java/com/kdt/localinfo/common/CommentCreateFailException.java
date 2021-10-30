package com.kdt.localinfo.common;

import lombok.Getter;
import org.springframework.validation.Errors;

@Getter
public class CommentCreateFailException extends RuntimeException {

    private Errors errors;

    public CommentCreateFailException(String message, Errors errors) {
        super(message);
        this.errors = errors;
    }

    public CommentCreateFailException(String message) {
        super(message);
    }
}
