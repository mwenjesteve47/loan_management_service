package com.example.loanmanagementservice.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ResourceNotFoundException extends RuntimeException {
    @JsonProperty("statusCode")
    Integer statusCode;
    public ResourceNotFoundException(String message) {
        super(message);
        log.error("ResourceNotFound exception thrown: " + message);
    }

    public ResourceNotFoundException(String message, Integer statusCode) {
        super(message);
        this.statusCode= statusCode;
        log.error("ResourceNotFound exception thrown: " + message);
    }
}
