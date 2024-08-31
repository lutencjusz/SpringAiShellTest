package com.example.SpringDocumentationAI.advices;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentConversionNotSupportedException;

@ControllerAdvice
public class HttpRequestMethodNotSupportedExceptionAdvice {
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)

    public ResponseEntity<String> handleMissingParams(HttpRequestMethodNotSupportedException ex) {
        String methodName = ex.getMethod();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(String.format("%s method is not supported, please use POST method", methodName));
    }
}

