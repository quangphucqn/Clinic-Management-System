package com.tqp.cms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DoctorCreationRequest {
    @NotNull(message = "FIELD_REQUIRED")
    UUID userId;

    @NotNull(message = "FIELD_REQUIRED")
    UUID specialtyId;

    @NotBlank(message = "FIELD_REQUIRED")
    @Size(max = 50, message = "VALIDATION_ERROR")
    String licenseNumber;

    @Size(max = 20, message = "VALIDATION_ERROR")
    String roomNumber;

    @NotNull(message = "FIELD_REQUIRED")
    @PositiveOrZero(message = "VALIDATION_ERROR")
    Integer yearsOfExperience;

    String biography;
}
