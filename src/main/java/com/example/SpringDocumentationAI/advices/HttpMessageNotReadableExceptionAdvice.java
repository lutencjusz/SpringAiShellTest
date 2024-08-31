package com.example.SpringDocumentationAI.advices;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class HttpMessageNotReadableExceptionAdvice {
    @ExceptionHandler(HttpMessageNotReadableException.class)

    public ResponseEntity<String> handleMissingParams(HttpMessageNotReadableException ex) {
        String fieldName = ex.getMessage();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(fieldName);
    }
}

