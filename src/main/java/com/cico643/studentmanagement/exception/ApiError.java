package com.cico643.studentmanagement.exception;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.Map;

@Data
public class ApiError {
    @JsonProperty("success")
    private boolean success = false;
    @JsonProperty("status")
    private HttpStatus status;
    @JsonProperty("message")
    private String message;
    @JsonProperty("data")
    private Map<String, Object> data = Collections.emptyMap();

    @JsonCreator
    ApiError(HttpStatus status) {
        this.status = status;
        this.message = "Unexpected error";
    }

    @JsonCreator
    ApiError(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
    @JsonCreator
    ApiError(HttpStatus status, String message, Map<String, Object> data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}
