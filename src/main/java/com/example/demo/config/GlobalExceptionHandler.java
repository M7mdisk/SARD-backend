package com.example.demo.config;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import java.security.SignatureException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(e.getMessage());
    }
    @ResponseBody
    @ExceptionHandler({ExpiredJwtException.class, SignatureException.class})
    public ResponseEntity<Object> handleOldToken(
            ExpiredJwtException exception,
            WebRequest request
    ) {
        System.out.println("Expired token occurred " + request.getContextPath());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Token is invalid or expired");

    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        System.out.println(e);
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred: " + e.getMessage());
    }
}
