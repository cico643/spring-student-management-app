package com.cico643.studentmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GenericApiResponse {
    private boolean success;
    private HttpStatus status;
    private String message;
    private Map<Object, Object> data;
}