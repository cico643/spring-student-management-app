package com.cico643.studentmanagement.service;

import com.cico643.studentmanagement.dto.CreateAssignmentRequest;
import com.cico643.studentmanagement.dto.GenericApiResponse;
import com.cico643.studentmanagement.exception.ApiError;
import com.cico643.studentmanagement.model.Assignment;
import com.cico643.studentmanagement.model.EnrollmentAssignment;
import com.cico643.studentmanagement.model.enumTypes.Role;
import com.cico643.studentmanagement.repository.AssignmentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AssignmentService {
    private final AssignmentRepository assignmentRepository;
    private final ClassService classService;
    private final AuthService authService;
    private final EnrollmentAssignmentService enrollmentAssignmentService;

    private final Logger log = LoggerFactory.getLogger(AuthService.class);

    public GenericApiResponse create(CreateAssignmentRequest body, HttpServletResponse response) throws IOException {
        var creatorUser = this.authService.getUserById(body.getCreatorId());
        var _class = this.classService.findClassById(body.getClassId());

        if(creatorUser.getRole() == Role.ADMIN || creatorUser.getRole() == Role.INSTRUCTOR) {
            if(creatorUser.getRole() == Role.INSTRUCTOR && !_class.getInstructor().getId().equals(creatorUser.getId())) {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                ApiError errorMessage = new ApiError(HttpStatus.FORBIDDEN,
                        "Assignment can only be created by the instructor of the class");
                new ObjectMapper().writeValue(response.getOutputStream(), errorMessage);
                return null;
            }
            var assignment = Assignment.builder()
                    .title(body.getTitle())
                    .description(body.getDescription())
                    ._class(_class)
                    .endDate(body.getEndDate())
                    .build();
            var savedAssignment = this.assignmentRepository.save(assignment);
            new Thread(() -> assignForAllEnrollments(savedAssignment)).start();
            log.info("The assignment with id: [" + savedAssignment.getId() +"] has been successfully created.");
            return GenericApiResponse.
                    <Assignment>builder()
                    .success(true)
                    .status(HttpStatus.CREATED)
                    .message("The assignment with id: [" + savedAssignment.getId() +"] has been successfully created.")
                    .data(savedAssignment)
                    .build();
        }
        response.setStatus(HttpStatus.FORBIDDEN.value());
        ApiError errorMessage = new ApiError(HttpStatus.FORBIDDEN,
                "Assignment cannot be created by this user");
        new ObjectMapper().writeValue(response.getOutputStream(), errorMessage);
        return null;
    }

    private void assignForAllEnrollments(Assignment savedAssignment) {
        var enrollments = savedAssignment.get_class().getEnrollments();
        enrollments.forEach(enrollment -> {
            var enrollmentAssignment = EnrollmentAssignment
                    .builder()
                    .assignment(savedAssignment)
                    .enrollment(enrollment)
                    .build();
            this.enrollmentAssignmentService.save(enrollmentAssignment);
        });
    }
}