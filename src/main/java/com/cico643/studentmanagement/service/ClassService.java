package com.cico643.studentmanagement.service;

import com.cico643.studentmanagement.dto.CreateClassRequest;
import com.cico643.studentmanagement.dto.GenericApiResponse;
import com.cico643.studentmanagement.model.Class;
import com.cico643.studentmanagement.repository.ClassRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

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
}
