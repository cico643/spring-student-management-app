package com.cico643.studentmanagement.controller;

import com.cico643.studentmanagement.dto.CreateEnrollmentRequest;
import com.cico643.studentmanagement.dto.UpdateGradeRequest;
import com.cico643.studentmanagement.dto.GenericApiResponse;
import com.cico643.studentmanagement.model.Enrollment;
import com.cico643.studentmanagement.service.EnrollmentService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
@RequestMapping("/api/v1/enrollment")
@RequiredArgsConstructor
public class EnrollmentController {
    private final EnrollmentService enrollmentService;

    @PostMapping
    public ResponseEntity<GenericApiResponse> create(@Valid @RequestBody CreateEnrollmentRequest request)  {
        return new ResponseEntity<>(enrollmentService.create(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenericApiResponse<Enrollment>> getClassById(@PathVariable int id) {
        return new ResponseEntity<>(enrollmentService.getEnrollmentById(id), HttpStatus.OK);
    }

    @PatchMapping("/{id}/grade")
    public ResponseEntity<GenericApiResponse> updateEnrollmentGrade(@PathVariable int id,
                                                                    @Valid @RequestBody UpdateGradeRequest body,
                                                                    HttpServletResponse response) throws IOException {
        return new ResponseEntity<>(enrollmentService.updateEnrollmentGrade(id, body, response), HttpStatus.OK);
    }
}
