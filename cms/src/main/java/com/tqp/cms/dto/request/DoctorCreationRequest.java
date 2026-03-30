package com.tqp.cms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
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
    @NotBlank(message = "FIELD_REQUIRED")
    @Size(min = 4, max = 50, message = "VALIDATION_ERROR")
    String username;

    @NotBlank(message = "FIELD_REQUIRED")
    @Size(min = 8, max = 100, message = "VALIDATION_ERROR")
    String password;

    @NotBlank(message = "FIELD_REQUIRED")
    @Size(max = 100, message = "VALIDATION_ERROR")
    String fullName;

    @NotBlank(message = "FIELD_REQUIRED")
    @Email(message = "VALIDATION_ERROR")
    @Size(max = 100, message = "VALIDATION_ERROR")
    String email;

    @Size(max = 15, message = "VALIDATION_ERROR")
    String phoneNumber;

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
