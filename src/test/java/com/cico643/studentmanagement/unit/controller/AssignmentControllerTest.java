package com.cico643.studentmanagement.unit.controller;

import com.cico643.studentmanagement.controller.AssignmentController;
import com.cico643.studentmanagement.dto.CreateAssignmentRequest;
import com.cico643.studentmanagement.dto.GenericApiResponse;
import com.cico643.studentmanagement.helper.FileLoader;
import com.cico643.studentmanagement.model.Assignment;
import com.cico643.studentmanagement.service.AssignmentService;
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

public class AssignmentControllerTest {

    private AssignmentController subject;
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private GenericApiResponse apiGenericSuccessResponse = new GenericApiResponse();
    private GenericApiResponse apiGenericFailureResponse = new GenericApiResponse();

    @Mock
    private AssignmentService assignmentService;

    @BeforeEach
    void setUp() throws Exception{
        MockitoAnnotations.openMocks(this);
        subject = new AssignmentController(assignmentService);
        apiGenericSuccessResponse.setSuccess(true);
        apiGenericFailureResponse.setSuccess(false);
    }

    @Test
    void create_shouldCreateAssignment() throws IOException {
        var jsonAssignmentTestData = FileLoader.read("classpath:AssignmentTestData.json");
        var parsedAssignment = mapper.readValue(jsonAssignmentTestData, Assignment.class);

        CreateAssignmentRequest createAssignmentRequest = new CreateAssignmentRequest(); // dummy object

        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

        apiGenericSuccessResponse.setMessage("The assignment with id: [" + parsedAssignment.getId() +"] has been successfully created.");
        apiGenericSuccessResponse.setStatus(HttpStatus.CREATED);
        apiGenericSuccessResponse.setData(parsedAssignment);

        given(this.assignmentService.create(createAssignmentRequest, mockHttpServletResponse)).willReturn(apiGenericSuccessResponse);

        var result = subject.create(createAssignmentRequest, mockHttpServletResponse);

        assertThat(result, is(new ResponseEntity<>(apiGenericSuccessResponse, HttpStatus.CREATED)));
    }
}
