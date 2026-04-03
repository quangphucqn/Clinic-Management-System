package com.tqp.cms.service;

import com.tqp.cms.dto.response.NotificationRealtimeResponse;
import com.tqp.cms.entity.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationRealtimePublisher {
    private final SimpMessagingTemplate messagingTemplate;

    public void publish(Notification notification) {
        NotificationRealtimeResponse payload = NotificationRealtimeResponse.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .content(notification.getContent())
                .createdAt(notification.getCreatedAt())
                .build();

        if (notification.getTargetUser() != null) {
            messagingTemplate.convertAndSendToUser(
                    notification.getTargetUser().getUsername(),
                    "/queue/notifications",
                    payload
            );
        }
        if (notification.getTargetRole() != null) {
            messagingTemplate.convertAndSend(
                    "/topic/notifications/role/" + notification.getTargetRole().name(),
                    payload
            );
        }
    }
}
