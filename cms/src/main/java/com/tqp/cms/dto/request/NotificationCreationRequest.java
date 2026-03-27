package com.tqp.cms.dto.request;

import com.tqp.cms.entity.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;
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
public class NotificationCreationRequest {
    @NotBlank(message = "FIELD_REQUIRED")
    @Size(max = 150, message = "VALIDATION_ERROR")
    String title;

    @NotBlank(message = "FIELD_REQUIRED")
    String content;

    UserRole targetRole;

    UUID targetUserId;

    LocalDateTime expiresAt;

    Boolean sentEmail;
}
