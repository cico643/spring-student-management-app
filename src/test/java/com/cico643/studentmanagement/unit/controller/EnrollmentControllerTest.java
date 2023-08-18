package com.cico643.studentmanagement.unit.controller;

import com.cico643.studentmanagement.controller.EnrollmentController;
import com.cico643.studentmanagement.dto.CreateEnrollmentRequest;
import com.cico643.studentmanagement.dto.GenericApiResponse;
import com.cico643.studentmanagement.dto.UpdateGradeRequest;
import com.cico643.studentmanagement.helper.FileLoader;
import com.cico643.studentmanagement.model.Enrollment;
import com.cico643.studentmanagement.service.EnrollmentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.mockito.BDDMockito.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class EnrollmentControllerTest {
    private EnrollmentController subject;
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private GenericApiResponse apiGenericSuccessResponse = new GenericApiResponse();
    private GenericApiResponse apiGenericFailureResponse = new GenericApiResponse();

    @Mock
    private EnrollmentService enrollmentService;

    @BeforeEach
    void setUp() throws Exception{
        MockitoAnnotations.openMocks(this);
        subject = new EnrollmentController(enrollmentService);
        apiGenericSuccessResponse.setSuccess(true);
        apiGenericFailureResponse.setSuccess(false);
    }

    @Test
    void create_shouldCreateEnrollment() throws JsonProcessingException {
        var jsonAssignmentTestData = FileLoader.read("classpath:EnrollmentTestData.json");
        var parsedEnrollment = mapper.readValue(jsonAssignmentTestData, Enrollment.class);

        CreateEnrollmentRequest createEnrollmentRequest = new CreateEnrollmentRequest(); // dummy object

        apiGenericSuccessResponse.setMessage("The enrollment with id: [" + parsedEnrollment.getId() +"] has been successfully created.");
        apiGenericSuccessResponse.setStatus(HttpStatus.CREATED);
        apiGenericSuccessResponse.setData(parsedEnrollment);

        given(this.enrollmentService.create(createEnrollmentRequest)).willReturn(apiGenericSuccessResponse);

        var result = this.subject.create(createEnrollmentRequest);

        assertThat(result, is(new ResponseEntity<>(apiGenericSuccessResponse, HttpStatus.CREATED)));
    }

    @Test
    void getEnrollmentById_shouldGetExactEnrollment() throws JsonProcessingException {
        var jsonAssignmentTestData = FileLoader.read("classpath:EnrollmentTestData.json");
        var parsedEnrollment = mapper.readValue(jsonAssignmentTestData, Enrollment.class);

        apiGenericSuccessResponse.setMessage("Fetched enrollment by id: [" + parsedEnrollment.getId() + "]");
        apiGenericSuccessResponse.setStatus(HttpStatus.OK);
        apiGenericSuccessResponse.setData(parsedEnrollment);

        given(this.enrollmentService.getEnrollmentById(parsedEnrollment.getId())).willReturn(apiGenericSuccessResponse);

        var result = this.subject.getEnrollmentById(parsedEnrollment.getId());

        assertThat(result, is(new ResponseEntity<>(apiGenericSuccessResponse, HttpStatus.OK)));
    }

    @Test
    void updateEnrollmentGrade_shouldUpdateGrade() throws IOException {
        var jsonAssignmentTestData = FileLoader.read("classpath:EnrollmentTestData.json");
        var parsedEnrollment = mapper.readValue(jsonAssignmentTestData, Enrollment.class);

        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

        UpdateGradeRequest updateGradeRequest = new UpdateGradeRequest();
        updateGradeRequest.setGrade(100);

        apiGenericSuccessResponse.setMessage("Updated grade of the enrollment [" + parsedEnrollment.getId() + "] to 100");
        apiGenericSuccessResponse.setStatus(HttpStatus.OK);
        parsedEnrollment.setGrade(100);
        apiGenericSuccessResponse.setData(parsedEnrollment);

        given(this.enrollmentService.updateEnrollmentGrade(parsedEnrollment.getId(),
                updateGradeRequest,
                mockHttpServletResponse)).willReturn(apiGenericSuccessResponse);

        var result = this.subject.updateEnrollmentGrade(parsedEnrollment.getId(),
                updateGradeRequest,
                mockHttpServletResponse);

        assertThat(result, is(new ResponseEntity<>(apiGenericSuccessResponse, HttpStatus.OK)));
    }
}