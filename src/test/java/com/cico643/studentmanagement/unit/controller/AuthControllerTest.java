package com.cico643.studentmanagement.unit.controller;

import com.cico643.studentmanagement.controller.AuthController;
import com.cico643.studentmanagement.dto.AuthResponse;
import com.cico643.studentmanagement.dto.LoginRequest;
import com.cico643.studentmanagement.dto.SignupRequest;
import com.cico643.studentmanagement.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.mockito.BDDMockito.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class AuthControllerTest {

    private AuthController subject;
    @Mock
    private AuthService authService;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        subject = new AuthController(authService);
    }

    @Test
    void signUp_shouldCreateNewUser() {
        var access_token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJpbnN0QHRlc3QuY29tIiwiaWF0IjoxNjkxNDQ2NTM4LCJleHAiOjE2OTE1MzI5Mzh9.LMqxTihfLY5Ktjaz7s7h8uRoCLikJNhRHINBPgXCTbA";
        var refresh_token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJpbnN0QHRlc3QuY29tIiwiaWF0IjoxNjkyMDI1NjUxLCJleHAiOjE2OTIxMTIwNTF9.iLzI_hlIETXyzQP8XaMh62OKgQC-e3F1jGF_NFmt4Pk";

        SignupRequest signupRequest = new SignupRequest(); // dummy object
        AuthResponse authResponse = AuthResponse
                            .builder()
                            .accessToken(access_token)
                            .refreshToken(refresh_token)
                            .build();
        given(this.authService.signup(signupRequest)).willReturn(authResponse);

        var result = this.subject.signup(signupRequest);

        assertThat(result, is(ResponseEntity.ok(authResponse)));
    }

    @Test
    void login_shouldLoginSuccessfully() {
        var access_token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJpbnN0QHRlc3QuY29tIiwiaWF0IjoxNjkxNDQ2NTM4LCJleHAiOjE2OTE1MzI5Mzh9.LMqxTihfLY5Ktjaz7s7h8uRoCLikJNhRHINBPgXCTbA";
        var refresh_token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJpbnN0QHRlc3QuY29tIiwiaWF0IjoxNjkyMDI1NjUxLCJleHAiOjE2OTIxMTIwNTF9.iLzI_hlIETXyzQP8XaMh62OKgQC-e3F1jGF_NFmt4Pk";

        LoginRequest loginRequest = new LoginRequest(); // dummy object
        AuthResponse authResponse = AuthResponse
                .builder()
                .accessToken(access_token)
                .refreshToken(refresh_token)
                .build();
        given(this.authService.authenticate(loginRequest)).willReturn(authResponse);

        var result = this.subject.login(loginRequest);

        assertThat(result, is(ResponseEntity.ok(authResponse)));
    }
}