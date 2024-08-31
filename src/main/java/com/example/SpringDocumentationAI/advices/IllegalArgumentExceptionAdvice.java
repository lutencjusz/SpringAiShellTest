package com.example.SpringDocumentationAI.advices;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class IllegalArgumentExceptionAdvice {
    @ExceptionHandler(IllegalArgumentException.class)

    public ResponseEntity<String> handleMissingParams(IllegalArgumentException ex) {
        String message = ex.getMessage();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(message);
    }
}

