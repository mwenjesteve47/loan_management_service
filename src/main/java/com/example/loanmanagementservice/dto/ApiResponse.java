package com.example.loanmanagementservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class ApiResponse<T> extends AbstractApiResponse implements Serializable {

    @JsonProperty("data")
    private T data;

    @JsonProperty("statusCode")
    Integer statusCode;

    public ApiResponse(Boolean success, String message) {
        super(success, message);
    }

    public ApiResponse(Boolean success, String message, T dataObject) {
        super(success, message);
        this.data = dataObject;
    }
    public ApiResponse(Boolean success, String message, T dataObject, Integer statusCode) {
        super(success, message);
        this.data = dataObject;
        this.statusCode= statusCode;
    }

    public ApiResponse(Boolean success, String message, Integer statusCode) {
        super(success, message);
        this.statusCode = statusCode;
    }

}
