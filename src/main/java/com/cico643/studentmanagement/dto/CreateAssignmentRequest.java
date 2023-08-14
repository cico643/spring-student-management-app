package com.cico643.studentmanagement.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UUID;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateAssignmentRequest {
    @Size(max = 120)
    @NotBlank
    @NotEmpty
    @NotNull
    private String title;

    @Size(max = 1500)
    @NotBlank
    @NotEmpty
    @NotNull
    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant endDate;

    private Integer classId;

    @UUID
    private String creatorId;
}