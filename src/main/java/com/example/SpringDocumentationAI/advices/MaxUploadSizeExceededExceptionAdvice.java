package com.example.SpringDocumentationAI.advices;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class MaxUploadSizeExceededExceptionAdvice {
    @ExceptionHandler(MaxUploadSizeExceededException.class)

    public ResponseEntity<String> handleMissingParams(MaxUploadSizeExceededException ex) {
        String fieldName = ex.getMessage();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(fieldName);
    }
}

