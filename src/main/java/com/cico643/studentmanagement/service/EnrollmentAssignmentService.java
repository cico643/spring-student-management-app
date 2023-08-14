package com.cico643.studentmanagement.service;

import com.cico643.studentmanagement.model.EnrollmentAssignment;
import com.cico643.studentmanagement.repository.EnrollmentAssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EnrollmentAssignmentService {
    private final EnrollmentAssignmentRepository enrollmentAssignmentRepository;

    public EnrollmentAssignment save(EnrollmentAssignment enrollmentAssignment) {
        return this.enrollmentAssignmentRepository.save(enrollmentAssignment);
    }
}