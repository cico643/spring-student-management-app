package com.cico643.studentmanagement.controller;

import com.cico643.studentmanagement.dto.CreateClassRequest;
import com.cico643.studentmanagement.dto.GenericApiResponse;
import com.cico643.studentmanagement.model.Class;
import com.cico643.studentmanagement.service.ClassService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/class")
@RequiredArgsConstructor
public class ClassController {
    private final ClassService classService;

    @PostMapping()
    public ResponseEntity<GenericApiResponse> create(@Valid @RequestBody CreateClassRequest request)  {
        return new ResponseEntity<>(classService.create(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenericApiResponse<Class>> getClassById(@PathVariable int id, HttpServletResponse response) throws IOException {
        return new ResponseEntity<>(classService.getClassById(id, response), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GenericApiResponse> deleteClassById(@PathVariable int id, HttpServletResponse response) throws IOException {
        return new ResponseEntity<>(classService.deleteClassById(id, response), HttpStatus.OK);
    }


}
