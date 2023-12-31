package com.cico643.studentmanagement.service;

import com.cico643.studentmanagement.dto.AuthResponse;
import com.cico643.studentmanagement.dto.LoginRequest;
import com.cico643.studentmanagement.dto.SignupRequest;
import com.cico643.studentmanagement.exception.UserNotFoundException;
import com.cico643.studentmanagement.model.Token;
import com.cico643.studentmanagement.model.User;
import com.cico643.studentmanagement.model.enumTypes.TokenType;
import com.cico643.studentmanagement.repository.TokenRepository;
import com.cico643.studentmanagement.repository.UserRepository;
import com.cico643.studentmanagement.security.jwt.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private final Logger log = LoggerFactory.getLogger(AuthService.class);

    public AuthResponse signup(SignupRequest request) {
        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();
        var savedUser = userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(savedUser, jwtToken);
        log.info("Signed up a new user with id: [" + savedUser.getId() + "]");
        return AuthResponse
                .builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthResponse authenticate(LoginRequest request) {
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User could not find by email: " + request.getEmail()));


        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        log.info("User logged in with id: [" + user.getId() + "]");
        return AuthResponse
                .builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if(userEmail != null) {
            var user = this.getUserByEmail(userEmail);
            if(jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthResponse
                        .builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

    public User getUserByEmail(String email) {
        return this.userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User could not find by email: " + email));
    }

    protected User getUserById(String id) {
        return this.userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User could not find by id: " + id));
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if(validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }
}
