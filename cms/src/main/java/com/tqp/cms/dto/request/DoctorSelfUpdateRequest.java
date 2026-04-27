package com.tqp.cms.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
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
public class DoctorSelfUpdateRequest {
    @Email(message = "VALIDATION_ERROR")
    @Size(max = 100, message = "VALIDATION_ERROR")
    String email;

    @Size(max = 15, message = "VALIDATION_ERROR")
    String phoneNumber;

    @Size(max = 50, message = "VALIDATION_ERROR")
    String licenseNumber;

    String biography;
}
