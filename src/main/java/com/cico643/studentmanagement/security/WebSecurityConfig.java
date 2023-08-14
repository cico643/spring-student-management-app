package com.cico643.studentmanagement.security;

import com.cico643.studentmanagement.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import static com.cico643.studentmanagement.model.enumTypes.Permission.*;
import static com.cico643.studentmanagement.model.enumTypes.Role.*;
import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;
    private final AccessDeniedHandler accessDeniedHandler;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    private static final String[] AUTH_WHITELIST = {
            "/api/v1/auth/**",
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.requestMatchers(AUTH_WHITELIST).permitAll()

                        .requestMatchers("/api/v1/class/**").hasAnyRole(ADMIN.name(), INSTRUCTOR.name())

                        .requestMatchers(GET, "/api/v1/class/**").hasAnyAuthority(ADMIN_READ.name(), INSTRUCTOR_READ.name())
                        .requestMatchers(POST, "/api/v1/class/**").hasAnyAuthority(ADMIN_CREATE.name(), INSTRUCTOR_CREATE.name())
                        .requestMatchers(PUT, "/api/v1/class/**").hasAnyAuthority(ADMIN_UPDATE.name(), INSTRUCTOR_UPDATE.name())
                        .requestMatchers(DELETE, "/api/v1/class/**").hasAnyAuthority(ADMIN_DELETE.name(), INSTRUCTOR_DELETE.name())

                        .requestMatchers("/api/v1/enrollment/**").hasAnyRole(ADMIN.name(), INSTRUCTOR.name(), STUDENT.name())
                        .requestMatchers(POST, "/api/v1/enrollment/**").hasAnyAuthority(ADMIN_CREATE.name(), STUDENT_CREATE.name())


                        .anyRequest().authenticated())
                .exceptionHandling(exceptionHandlingConfigurer ->
                        exceptionHandlingConfigurer
                                .authenticationEntryPoint(authenticationEntryPoint)
                                .accessDeniedHandler(accessDeniedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(httpSecurityLogoutConfigurer ->
                        httpSecurityLogoutConfigurer
                                .logoutUrl("/api/v1/auth/logout")
                                .addLogoutHandler(logoutHandler)
                                .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
                )
                .build();
    }
}
