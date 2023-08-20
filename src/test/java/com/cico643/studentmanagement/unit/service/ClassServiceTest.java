package com.cico643.studentmanagement.unit.service;

import com.cico643.studentmanagement.dto.CreateClassRequest;
import com.cico643.studentmanagement.dto.GenericApiResponse;
import com.cico643.studentmanagement.exception.KlassNotFoundException;
import com.cico643.studentmanagement.helper.FileLoader;
import com.cico643.studentmanagement.model.Class;
import com.cico643.studentmanagement.model.User;
import com.cico643.studentmanagement.model.enumTypes.Role;
import com.cico643.studentmanagement.repository.ClassRepository;
import com.cico643.studentmanagement.service.AuthService;
import com.cico643.studentmanagement.service.ClassService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static org.mockito.BDDMockito.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
public class ClassServiceTest {
    private ClassService subject;

    private final ObjectMapper mapper = new ObjectMapper();


    @Mock
    private ClassRepository classRepository;
    @Mock
    private AuthService authService;

    @Mock
    private Authentication auth;

    @Mock
    private UserDetails principal;

    private GenericApiResponse apiGenericSuccessResponse = new GenericApiResponse();
    private GenericApiResponse apiGenericFailureResponse = new GenericApiResponse();

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        subject = new ClassService(classRepository, authService);
        apiGenericSuccessResponse.setSuccess(true);
        apiGenericFailureResponse.setSuccess(false);

        // init security context
        given(principal.getUsername()).willReturn("inst@test.com");
        given(auth.getPrincipal()).willReturn(principal);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void create_shouldCreateClass() throws JsonProcessingException {
        var jsonUserTestData = FileLoader.read("classpath:UserTestData.json");
        var parsedUser = mapper.readValue(jsonUserTestData, User.class);

        var jsonClassTestData = FileLoader.read("classpath:ClassTestData.json");
        var parsedClass = mapper.readValue(jsonClassTestData, Class.class);
        parsedClass.setInstructor(parsedUser);

        CreateClassRequest createClassRequest = new CreateClassRequest();
        createClassRequest.setTitle(parsedClass.getTitle());

        var _class = Class
                .builder()
                .title(parsedClass.getTitle())
                .instructor(parsedUser)
                .build();

        given(this.authService.getUserByEmail(parsedUser.getEmail())).willReturn(parsedUser);
        given(this.classRepository.save(_class)).willReturn(parsedClass);

        apiGenericSuccessResponse.setMessage("The class named: [" + parsedClass.getTitle() +"] has been successfully created.");
        apiGenericSuccessResponse.setStatus(HttpStatus.CREATED);
        apiGenericSuccessResponse.setData(Map.of("id", parsedClass.getId(), "title", parsedClass.getTitle()));

        var result = subject.create(createClassRequest);

        assertThat(result, is(apiGenericSuccessResponse));
    }

    @Test
    void getClassById_shouldGetClass_whenInstructorOfClassCalls() throws IOException {
        var jsonUserTestData = FileLoader.read("classpath:UserTestData.json");
        var parsedUser = mapper.readValue(jsonUserTestData, User.class);

        var jsonClassTestData = FileLoader.read("classpath:ClassTestData.json");
        var parsedClass = mapper.readValue(jsonClassTestData, Class.class);
        parsedClass.setInstructor(parsedUser);

        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

        given(this.classRepository.findById(parsedClass.getId())).willReturn(Optional.of(parsedClass));
        given(this.authService.getUserByEmail(parsedUser.getEmail())).willReturn(parsedUser);

        apiGenericSuccessResponse.setMessage("Fetched class by id: [" + parsedClass.getId() + "]");
        apiGenericSuccessResponse.setStatus(HttpStatus.OK);
        apiGenericSuccessResponse.setData(parsedClass);

        var result = subject.getClassById(parsedClass.getId(), mockHttpServletResponse);

        assertThat(result, is(apiGenericSuccessResponse));
    }

    @Test
    void getClassById_shouldGetClass_whenAdminCalls() throws IOException {
        var jsonUserTestData = FileLoader.read("classpath:UserTestData.json");
        var parsedUser = mapper.readValue(jsonUserTestData, User.class);

        var jsonClassTestData = FileLoader.read("classpath:ClassTestData.json");
        var parsedClass = mapper.readValue(jsonClassTestData, Class.class);
        parsedClass.setInstructor(parsedUser);
        parsedUser.setRole(Role.ADMIN);

        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

        given(this.classRepository.findById(parsedClass.getId())).willReturn(Optional.of(parsedClass));
        given(this.authService.getUserByEmail(parsedUser.getEmail())).willReturn(parsedUser);

        apiGenericSuccessResponse.setMessage("Fetched class by id: [" + parsedClass.getId() + "]");
        apiGenericSuccessResponse.setStatus(HttpStatus.OK);
        apiGenericSuccessResponse.setData(parsedClass);

        var result = subject.getClassById(parsedClass.getId(), mockHttpServletResponse);

        assertThat(result, is(apiGenericSuccessResponse));
    }

    @Test
    void getClassById_shouldThrowKlassNotFound_whenGivenIdDoesNotMatchAnyRecord() {
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

        given(this.classRepository.findById(anyInt()))
                .willThrow(new KlassNotFoundException("Class could not find by given id: [1]"));
        KlassNotFoundException thrown = Assertions.assertThrows(KlassNotFoundException.class,
                () -> subject.getClassById(1, mockHttpServletResponse));
        assertThat(thrown.getMessage(), is("Class could not find by given id: [1]"));
    }

    @Test
    void getClassById_shouldThrowAccessDenied_whenInstructorIsNotInChargeOfTheClass() throws IOException {
        var jsonUserTestData = FileLoader.read("classpath:UserTestData.json");
        var parsedUser = mapper.readValue(jsonUserTestData, User.class);
        var fakeUser = mapper.readValue(jsonUserTestData, User.class);
        fakeUser.setId("gibberish");

        var jsonClassTestData = FileLoader.read("classpath:ClassTestData.json");
        var parsedClass = mapper.readValue(jsonClassTestData, Class.class);

        parsedClass.setInstructor(parsedUser);
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

        given(this.classRepository.findById(parsedClass.getId())).willReturn(Optional.of(parsedClass));
        given(this.authService.getUserByEmail(parsedUser.getEmail())).willReturn(fakeUser);

        var result = subject.getClassById(parsedClass.getId(), mockHttpServletResponse);
        Assertions.assertNull(result);
    }
}
