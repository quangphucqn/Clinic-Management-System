package com.tqp.cms.dto.request;

import com.tqp.cms.entity.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PatientRegistrationRequest {
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

    Gender gender;

    LocalDate dateOfBirth;

    @Size(max = 255, message = "VALIDATION_ERROR")
    String address;

    @Size(max = 100, message = "VALIDATION_ERROR")
    String emergencyContactName;

    @Size(max = 15, message = "VALIDATION_ERROR")
    String emergencyContactPhone;
}
