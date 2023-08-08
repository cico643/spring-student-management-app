package com.cico643.studentmanagement.service;

import com.cico643.studentmanagement.dto.CreateClassRequest;
import com.cico643.studentmanagement.dto.GenericApiResponse;
import com.cico643.studentmanagement.exception.ApiError;
import com.cico643.studentmanagement.exception.KlassNotFoundException;
import com.cico643.studentmanagement.model.Class;
import com.cico643.studentmanagement.model.enumTypes.Role;
import com.cico643.studentmanagement.repository.ClassRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ClassService {

    private final ClassRepository classRepository;
    private final AuthService authService;

    private final Logger log = LoggerFactory.getLogger(ClassService.class);

    public GenericApiResponse create(CreateClassRequest request)  {
        var principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var user = this.authService.getUserByEmail(principal.getUsername());
        var _class = Class.builder()
                .title(request.getTitle())
                .instructor(user)
                .build();
        this.classRepository.save(_class);
        log.info("A class created by instructor id: [" + _class.getInstructor().getId() + "]");
        return GenericApiResponse
                .builder()
                .success(true)
                .status(HttpStatus.CREATED)
                .message("The class named: [" + _class.getTitle() +"] has been successfully created.")
                .data(Map.of("id", _class.getId(), "title", _class.getTitle()))
                .build();
    }

    public GenericApiResponse<Class> getClassById(Integer id, HttpServletResponse response) throws IOException, KlassNotFoundException {
        GenericApiResponse<Class> result = null;
        boolean finished = false;
        var _class = findClassById(id);
        log.info("Fetched class by id: [" + id + "]");

        var principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var user = this.authService.getUserByEmail(principal.getUsername());
        if (user.getRole() != Role.ADMIN) {
            if (!_class.getInstructor().getId().equals(user.getId())) {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                ApiError errorMessage = new ApiError(HttpStatus.FORBIDDEN, "Access Denied");
                new ObjectMapper().writeValue(response.getOutputStream(), errorMessage);
                finished = true;
            }
        }
        if (!finished) {
            result = GenericApiResponse
                    .<Class>builder()
                    .success(true)
                    .status(HttpStatus.OK)
                    .message("Fetched class by id: [" + id + "]")
                    .data(_class)
                    .build();

        }

        return result;
    }

    public GenericApiResponse deleteClassById(int id, HttpServletResponse response) throws IOException, KlassNotFoundException {
        GenericApiResponse result = null;
        boolean finished = false;
        var _class = findClassById(id);
        log.info("Fetched class by id: [" + id + "]");

        var principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var user = this.authService.getUserByEmail(principal.getUsername());
        if (user.getRole() != Role.ADMIN) {
            if (!_class.getInstructor().getId().equals(user.getId())) {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                ApiError errorMessage = new ApiError(HttpStatus.FORBIDDEN,
                        "Class can only be deleted by an authorized user");
                new ObjectMapper().writeValue(response.getOutputStream(), errorMessage);
                finished = true;
            }
        }
        if (!finished) {
            this.classRepository.deleteById(id);
            log.info("Deleted class by id: [" + id + "]");
            result = GenericApiResponse
                    .builder()
                    .success(true)
                    .status(HttpStatus.OK)
                    .message("Deleted class by id: [" + id + "]")
                    .build();
        }

        return result;
    }

    protected Class findClassById(int id) throws KlassNotFoundException {
        return this.classRepository.findById(id)
                .orElseThrow(() -> new KlassNotFoundException("Class could not find by given id: [" + id + "]"));
    }
}
