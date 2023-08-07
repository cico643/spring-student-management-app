package com.cico643.studentmanagement.controller;

import com.cico643.studentmanagement.dto.CreateClassRequest;
import com.cico643.studentmanagement.dto.GenericApiResponse;
import com.cico643.studentmanagement.service.ClassService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/class")
@RequiredArgsConstructor
public class ClassController {
    private final ClassService classService;

    @PostMapping()
    public ResponseEntity<GenericApiResponse> create(@Valid @RequestBody CreateClassRequest request)  {
        return new ResponseEntity<>(classService.create(request), HttpStatus.CREATED);
    }
}
