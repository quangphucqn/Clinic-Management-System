package com.tqp.cms.dto.request;

import jakarta.validation.constraints.NotBlank;
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
public class ChangePasswordRequest {
    @NotBlank(message = "FIELD_REQUIRED")
    @Size(min = 8, max = 100, message = "VALIDATION_ERROR")
    String currentPassword;

    @NotBlank(message = "FIELD_REQUIRED")
    @Size(min = 8, max = 100, message = "VALIDATION_ERROR")
    String newPassword;
}
