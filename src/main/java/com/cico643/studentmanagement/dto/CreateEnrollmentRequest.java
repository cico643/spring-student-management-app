package com.cico643.studentmanagement.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateEnrollmentRequest {
    @NotNull
    @NotEmpty
    @UUID
    private String student_id;

    private int class_id;
}
