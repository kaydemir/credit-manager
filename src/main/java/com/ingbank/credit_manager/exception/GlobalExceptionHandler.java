package com.ingbank.credit_manager.exception;

import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice("com.ingbank.credit_manager")
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException ex) {
        log.error("Bad request - {}", ex.getMessage());
        return ResponseEntity.badRequest().body(ex.getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining()));
    }

    @ExceptionHandler({ IllegalStateException.class })
    public ResponseEntity<Object> handleIllegalStateException(final IllegalStateException ex) {
        log.warn("Illegal state - {}", ex.getMessage());
        return ResponseEntity.badRequest().body("Illegal state: " + ex.getMessage());
    }

    @ExceptionHandler({ TypeMismatchException.class })
    public ResponseEntity<Object> handleTypeMismatchException(final TypeMismatchException ex) {
        log.warn("Type mismatch - {}", ex.getMessage());
        return ResponseEntity.internalServerError().body("Type mismatch: " + ex.getMessage());
    }

    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<Object> handleRoleException(final AccessDeniedException ex) {
        log.error("Access denied - {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Insufficient permission: " + ex.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentialsException(BadCredentialsException ex) {
        log.error("Bad Credentials {} ", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bad credentials: " + ex.getMessage());
    }

    @ExceptionHandler({ BeanCreationException.class })
    public ResponseEntity<Object> handleBeanCreationException(final BeanCreationException ex) {
        log.error("Bean cannot be created {} ", ex.getBeanName());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Bean cannot be created: " + ex.getMessage());
    }

    @ExceptionHandler({UsernameNotFoundException.class})
    public ResponseEntity<Object> handleUsernameNotFoundException(final UsernameNotFoundException ex) {
        log.warn("User not found - {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found : " + ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        log.error("An error occurred: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + ex.getMessage());
    }
}