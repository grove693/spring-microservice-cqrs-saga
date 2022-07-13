package com.learning.products.core.errorhandling;

import org.axonframework.commandhandling.CommandExecutionException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@ControllerAdvice
public class ProductsServiceErrorHandler {

    @ExceptionHandler(value = {IllegalStateException.class})
    public ResponseEntity<Object> handleIllegalStateException(IllegalStateException ex, WebRequest req) {

        ErrorMessage err = new ErrorMessage(new Date(), ex.getMessage());

        return new ResponseEntity<>(err,new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<Object> handleOtherExceptions(Exception ex, WebRequest req) {

        ErrorMessage err = new ErrorMessage(new Date(), ex.getMessage());

        return new ResponseEntity<>(err,new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {CommandExecutionException.class})
    public ResponseEntity<Object> handleCommandExecutionExceptions(Exception ex, WebRequest req) {

        ErrorMessage err = new ErrorMessage(new Date(), ex.getMessage());

        return new ResponseEntity<>(err,new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
