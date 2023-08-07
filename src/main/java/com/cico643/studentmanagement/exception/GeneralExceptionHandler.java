package com.cico643.studentmanagement.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.validation.constraints.NotNull;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GeneralExceptionHandler extends ResponseEntityExceptionHandler {

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    @NotNull
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  @NotNull HttpHeaders headers,
                                                                  @NotNull HttpStatusCode status,
                                                                  @NotNull WebRequest request) {
        Map<String, Object> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error ->{
            String fieldName = ((FieldError) error).getField();
            Object errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        ApiError errorMessage = new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage(), errors);

        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> userNotFoundExceptionHandler(UserNotFoundException exception) throws JsonProcessingException {
        ApiError errorMessage = new ApiError(HttpStatus.NOT_FOUND, exception.getMessage());
        return new ResponseEntity<>(mapper.writeValueAsString(errorMessage), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(KlassNotFoundException.class)
    public ResponseEntity<?> classNotFoundExceptionHandler(UserNotFoundException exception) throws JsonProcessingException {
        ApiError errorMessage = new ApiError(HttpStatus.NOT_FOUND, exception.getMessage());
        return new ResponseEntity<>(mapper.writeValueAsString(errorMessage), HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> conflictExceptionHandler(DataIntegrityViolationException exception) throws JsonProcessingException {
        String message = NestedExceptionUtils.getMostSpecificCause(exception).getMessage();
        ApiError errorMessage = new ApiError(HttpStatus.CONFLICT, message);
        return new ResponseEntity<>(mapper.writeValueAsString(errorMessage), HttpStatus.CONFLICT);
    }
}
