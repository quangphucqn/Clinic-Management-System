package com.tqp.cms.dto.request;

import com.tqp.cms.entity.UserRole;
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
public class UserUpdateRequest {
    @Size(min = 4, max = 50, message = "VALIDATION_ERROR")
    String username;

    @Size(min = 8, max = 100, message = "VALIDATION_ERROR")
    String password;

    @Size(max = 100, message = "VALIDATION_ERROR")
    String fullName;

    @Email(message = "VALIDATION_ERROR")
    @Size(max = 100, message = "VALIDATION_ERROR")
    String email;

    @Size(max = 15, message = "VALIDATION_ERROR")
    String phoneNumber;

    UserRole role;
}
