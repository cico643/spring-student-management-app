package com.cico643.studentmanagement.dto;

import com.cico643.studentmanagement.model.enumTypes.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequest {
    @NotNull
    @NotEmpty
    private String firstName;
    @NotNull
    @NotEmpty
    private String lastName;
    @Email
    private String email;
    @Size(min = 6)
    @NotEmpty
    @NotNull
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;
}
