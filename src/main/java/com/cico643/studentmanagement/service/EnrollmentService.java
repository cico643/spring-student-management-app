package com.cico643.studentmanagement.service;

import com.cico643.studentmanagement.dto.CreateEnrollmentRequest;
import com.cico643.studentmanagement.dto.GenericApiResponse;
import com.cico643.studentmanagement.exception.EnrollmentNotFoundException;
import com.cico643.studentmanagement.exception.KlassNotFoundException;
import com.cico643.studentmanagement.model.Enrollment;
import com.cico643.studentmanagement.model.enumTypes.Role;
import com.cico643.studentmanagement.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final ClassService classService;
    private final AuthService authService;

    private final Logger log = LoggerFactory.getLogger(EnrollmentService.class);
    public GenericApiResponse create(CreateEnrollmentRequest request) throws KlassNotFoundException {
            var _class = this.classService.findClassById(request.getClass_id());
            var requestUser = this.authService.getUserById(request.getStudent_id());
            if (requestUser.getRole() != Role.STUDENT) {
                throw new RuntimeException("Only a user with a student role can attend for the class");
            }
        Enrollment enrollment = Enrollment
                .builder()
                .student(requestUser)
                ._class(_class)
                .build();
        var savedEnrollment = this.enrollmentRepository.save(enrollment);
        log.info("Student [" + request.getStudent_id() + "] has enrolled for class [" + _class.getId() + "]");
        return GenericApiResponse
                .<Enrollment>builder()
                .success(true)
                .status(HttpStatus.CREATED)
                .message("The enrollment with id: [" + savedEnrollment.getId() +"] has been successfully created.")
                .data(savedEnrollment)
                .build();
    }

    public GenericApiResponse getEnrollmentById(int id) {
        var enrollment = this.enrollmentRepository.findById(id)
                .orElseThrow(() -> new EnrollmentNotFoundException("Enrollment could not find by given id: [" + id + "]")
        );

        return GenericApiResponse
                .<Enrollment>builder()
                .success(true)
                .status(HttpStatus.OK)
                .message("Fetched enrollment by id: [" + id + "]")
                .data(enrollment)
                .build();
    }
}
