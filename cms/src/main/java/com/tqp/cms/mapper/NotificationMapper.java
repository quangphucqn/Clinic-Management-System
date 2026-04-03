package com.tqp.cms.mapper;

import com.tqp.cms.dto.response.NotificationListResponse;
import com.tqp.cms.dto.response.NotificationResponse;
import com.tqp.cms.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {
    public NotificationListResponse toListResponse(Notification notification) {
        return NotificationListResponse.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .createdAt(notification.getCreatedAt())
                .build();
    }

    public NotificationResponse toAdminDetailResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .content(notification.getContent())
                .emailSent(notification.getEmailSent())
                .targetRole(notification.getTargetRole())
                .targetUserId(notification.getTargetUser() != null ? notification.getTargetUser().getId() : null)
                .targetUsername(notification.getTargetUser() != null ? notification.getTargetUser().getUsername() : null)
                .targetUserEmail(notification.getTargetUser() != null ? notification.getTargetUser().getEmail() : null)
                .active(notification.isActive())
                .createdAt(notification.getCreatedAt())
                .updatedAt(notification.getUpdatedAt())
                .build();
    }

    public NotificationResponse toNonAdminDetailResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .content(notification.getContent())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
