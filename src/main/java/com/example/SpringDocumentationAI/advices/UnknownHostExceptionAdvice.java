package com.example.SpringDocumentationAI.advices;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.UnknownHostException;

@Slf4j
@ControllerAdvice
public class UnknownHostExceptionAdvice {
    @ExceptionHandler(UnknownHostException.class)

    public ResponseEntity<String> handleMissingParams(UnknownHostException ex) {
        log.error("UnknownHostException: {}", ex.getMessage());
        String message = ex.getMessage();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(message);
    }
}

