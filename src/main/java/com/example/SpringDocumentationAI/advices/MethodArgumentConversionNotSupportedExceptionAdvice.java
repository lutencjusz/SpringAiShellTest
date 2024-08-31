package com.example.SpringDocumentationAI.advices;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentConversionNotSupportedException;

@ControllerAdvice
public class MethodArgumentConversionNotSupportedExceptionAdvice {
    @ExceptionHandler(MethodArgumentConversionNotSupportedException.class)

    public ResponseEntity<String> handleMissingParams(MethodArgumentConversionNotSupportedException ex) {
        String fieldName = ex.getName();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(String.format("Field '%s' cannot be empty", fieldName));
    }
}

