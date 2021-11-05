package com.kdt.localinfo.error;

import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.Errors;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartException;

import javax.persistence.EntityNotFoundException;

@Slf4j
@ControllerAdvice
public class GeneralExceptionHandler {

    private ResponseEntity<Errors> newResponse(HttpStatus status) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/hal+json;charset=UTF-8");
        return new ResponseEntity<>(headers, status);
    }

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<EntityModel<Errors>> badRequest(InvalidInputException ex) {
        return ResponseEntity.badRequest().body(ErrorResources.modelOf(ex.getErrors()));
    }


    @ExceptionHandler({
            NotFoundException.class, EntityNotFoundException.class
    })
    public ResponseEntity<EntityModel<Errors>> notFound() {
        return ResponseEntity.notFound().build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
            IllegalStateException.class, IllegalArgumentException.class,
            TypeMismatchException.class, HttpMessageNotReadableException.class,
            MissingServletRequestParameterException.class, MultipartException.class,
    })
    public ResponseEntity<EntityModel<Errors>> handleBadRequestException(Exception e) {
        log.error("*** BadRequestException ***");
        e.printStackTrace();
        return ResponseEntity.badRequest().build();
    }

    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler(HttpMediaTypeException.class)
    public ResponseEntity<?> handleHttpMediaTypeException(Exception e) {
        log.error("*** HttpMediaTypeException ***");
        e.printStackTrace();
        return newResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> handleMethodNotAllowedException(Exception e) {
        log.error("*** MethodNotAllowedException ***");
        e.printStackTrace();
        return newResponse(HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler({Exception.class, RuntimeException.class})
    public ResponseEntity<?> handleException(Exception e) {
        log.error("Unexpected exception occurred: {}", e.getMessage(), e);
        return ResponseEntity.internalServerError().build();
    }

}
