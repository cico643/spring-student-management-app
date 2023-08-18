package com.cico643.studentmanagement.unit.controller;

import com.cico643.studentmanagement.controller.ClassController;
import com.cico643.studentmanagement.dto.CreateClassRequest;
import com.cico643.studentmanagement.dto.GenericApiResponse;
import com.cico643.studentmanagement.helper.FileLoader;
import com.cico643.studentmanagement.model.Class;
import com.cico643.studentmanagement.service.ClassService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.util.Map;

import static org.mockito.BDDMockito.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

class ClassControllerTest {

    private ClassController subject;
    private GenericApiResponse apiGenericSuccessResponse = new GenericApiResponse();
    private GenericApiResponse apiGenericFailureResponse = new GenericApiResponse();

    @Mock
    private ClassService classService;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        subject = new ClassController(classService);
        apiGenericSuccessResponse.setSuccess(true);
        apiGenericFailureResponse.setSuccess(false);
    }

    @Test
    void create_shouldCreateClass() throws JsonProcessingException {
        var jsonClassTestData = FileLoader.read("classpath:ClassTestData.json");
        var parsedClass = new ObjectMapper().readValue(jsonClassTestData, Class.class);

        CreateClassRequest createClassRequest = new CreateClassRequest(); // dummy object

        apiGenericSuccessResponse.setMessage("The class named: [" + parsedClass.getTitle() +"] has been successfully created.");
        apiGenericSuccessResponse.setStatus(HttpStatus.CREATED);
        apiGenericSuccessResponse.setData(Map.of("id", parsedClass.getId(), "title", parsedClass.getTitle()));

        given(classService.create(createClassRequest)).willReturn(apiGenericSuccessResponse);

        var result = subject.create(createClassRequest);

        assertThat(result, is(new ResponseEntity<>(apiGenericSuccessResponse, HttpStatus.CREATED)));
    }

    @Test
    void getClassById_shouldGet() throws IOException {
        var jsonClassTestData = FileLoader.read("classpath:ClassTestData.json");
        var parsedClass = new ObjectMapper().readValue(jsonClassTestData, Class.class);

        MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();
        // HttpServletResponse httpServletResponse = mock(HttpServletResponse.class); Alternatively we could have mock
        // the actual class by ourselves

        apiGenericSuccessResponse.setMessage("Fetched class by id: [" + parsedClass.getId() + "]");
        apiGenericSuccessResponse.setStatus(HttpStatus.OK);
        apiGenericSuccessResponse.setData(parsedClass);

        given(classService.getClassById(parsedClass.getId(), httpServletResponse)).willReturn(apiGenericSuccessResponse);

        var result = subject.getClassById(parsedClass.getId(), httpServletResponse);

        assertThat(result, is(new ResponseEntity<>(apiGenericSuccessResponse, HttpStatus.OK)));
    }

    @Test
    void deleteClassById_shouldDelete() throws IOException {
        MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();

        apiGenericSuccessResponse.setMessage("Deleted class by id: [1]");
        apiGenericSuccessResponse.setStatus(HttpStatus.OK);

        given(classService.deleteClassById(1, httpServletResponse)).willReturn(apiGenericSuccessResponse);

        var result = subject.deleteClassById(1, httpServletResponse);

        assertThat(result, is(new ResponseEntity<>(apiGenericSuccessResponse, HttpStatus.OK)));
    }

    @Test
    void getEnrollmentsForGivenClass_shouldGet() throws JsonProcessingException {
        var jsonClassTestData = FileLoader.read("classpath:ClassTestData.json");
        var parsedClass = new ObjectMapper().readValue(jsonClassTestData, Class.class);

        apiGenericSuccessResponse.setMessage("Fetched all enrollments of given class id: [" + parsedClass.getId() + "]");
        apiGenericSuccessResponse.setStatus(HttpStatus.OK);
        apiGenericSuccessResponse.setData(parsedClass.getEnrollments());

        given(classService.getAllEnrollmentsForGivenClass(parsedClass.getId())).willReturn(apiGenericSuccessResponse);

        var result = subject.getEnrollmentsForGivenClass(parsedClass.getId());

        assertThat(result, is(new ResponseEntity<>(apiGenericSuccessResponse, HttpStatus.OK)));
    }
}