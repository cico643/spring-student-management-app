package com.cico643.studentmanagement.exception;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.format.DateTimeFormatter;

@Data
public class ApiError {
    @JsonProperty("status")
    private HttpStatus status;
    @JsonProperty("timestamp")
    private String timestamp;
    @JsonProperty("message")
    private String message;

    @JsonCreator
    private ApiError() {
        timestamp = java.time.LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
    }

    @JsonCreator
    ApiError(HttpStatus status) {
        this();
        this.status = status;
        this.message = "Unexpected error";
    }

    @JsonCreator
    ApiError(HttpStatus status, String message) {
        this();
        this.status = status;
        this.message = message;
    }
}
