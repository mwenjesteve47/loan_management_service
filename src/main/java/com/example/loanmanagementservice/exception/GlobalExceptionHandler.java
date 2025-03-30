package com.example.loanmanagementservice.exception;

import com.example.loanmanagementservice.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice

public class GlobalExceptionHandler extends RuntimeException {
    // Handle ResourceNotFoundException
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFound(ResourceNotFoundException ex) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", false);
        response.put("message", ex.getMessage());
        response.put("timestamp", LocalDateTime.now());

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // Handle all other exceptions (optional)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(Exception ex) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", false);
        response.put("message", "An unexpected error occurred.");
        response.put("error", ex.getMessage());
        response.put("timestamp", LocalDateTime.now());

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, ex.getMessage()));
    }
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ApiResponse<String>> handleDuplicateResourceException(InvalidRequestException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse<>(false, ex.getMessage()));
    }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
            Map<String, String> errors = new HashMap<>();
            ex.getBindingResult().getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage()));
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

}
