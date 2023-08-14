package com.cico643.studentmanagement.controller;

import com.cico643.studentmanagement.dto.CreateAssignmentRequest;
import com.cico643.studentmanagement.dto.GenericApiResponse;
import com.cico643.studentmanagement.service.AssignmentService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/assignment")
@RequiredArgsConstructor
public class AssignmentController {
    private final AssignmentService assignmentService;

    @PostMapping
    public ResponseEntity<GenericApiResponse> create(@Valid @RequestBody CreateAssignmentRequest body,
                                                     HttpServletResponse response) throws IOException {
        return new ResponseEntity<>(assignmentService.create(body, response), HttpStatus.CREATED);
    }
}