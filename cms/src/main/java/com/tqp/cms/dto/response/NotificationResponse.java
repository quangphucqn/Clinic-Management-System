package com.tqp.cms.dto.response;

import com.tqp.cms.entity.UserRole;
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
public class NotificationResponse {
    UUID id;
    String title;
    String content;
    Boolean emailSent;
    UserRole targetRole;
    UUID targetUserId;
    String targetUsername;
    String targetUserEmail;
    LocalDateTime expiresAt;
    boolean active;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
