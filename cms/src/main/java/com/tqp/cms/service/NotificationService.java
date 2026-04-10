package com.tqp.cms.service;

import com.tqp.cms.dto.request.NotificationCreationRequest;
import com.tqp.cms.dto.request.NotificationUpdateRequest;
import com.tqp.cms.dto.response.NotificationListResponse;
import com.tqp.cms.dto.response.NotificationResponse;
import java.util.UUID;
import org.springframework.data.domain.Page;

public interface NotificationService {
    NotificationResponse createNotification(NotificationCreationRequest request);

    Page<NotificationListResponse> getNotifications(int page, int size, String title);

    NotificationResponse getNotificationById(UUID notificationId);

    NotificationResponse updateNotification(UUID notificationId, NotificationUpdateRequest request);

    void softDeleteNotification(UUID notificationId);

    Page<NotificationListResponse> getMyNotifications(int page, int size);
}
