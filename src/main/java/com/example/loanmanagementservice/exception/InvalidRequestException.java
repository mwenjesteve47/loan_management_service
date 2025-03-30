package com.example.loanmanagementservice.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.slf4j.Slf4j;

@Slf4j

public class InvalidRequestException extends RuntimeException {

    public InvalidRequestException(String message) {
        super(message);
        log.error("ResourceNotFound exception thrown: " + message);
    }

}
