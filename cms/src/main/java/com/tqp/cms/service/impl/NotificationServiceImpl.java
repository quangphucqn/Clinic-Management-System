package com.tqp.cms.service.impl;

import com.tqp.cms.dto.request.NotificationCreationRequest;
import com.tqp.cms.dto.request.NotificationUpdateRequest;
import com.tqp.cms.dto.response.NotificationResponse;
import com.tqp.cms.entity.Notification;
import com.tqp.cms.exception.AppException;
import com.tqp.cms.exception.ErrorCode;
import com.tqp.cms.repository.NotificationRepository;
import com.tqp.cms.repository.UsersRepository;
import com.tqp.cms.service.NotificationService;
import com.tqp.cms.service.SendGridEmailService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationServiceImpl implements NotificationService {
    NotificationRepository notificationRepository;
    UsersRepository usersRepository;
    SendGridEmailService sendGridEmailService;

    @Override
    @Transactional
    public NotificationResponse createNotification(NotificationCreationRequest request) {
        validateTarget(request.getTargetRole(), request.getTargetUserId());
        var targetUser = resolveTargetUser(request.getTargetUserId());

        Notification notification = Notification.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .targetRole(request.getTargetRole())
                .targetUser(targetUser)
                .expiresAt(request.getExpiresAt())
                .emailSent(false)
                .build();

        notification = notificationRepository.save(notification);

        if (Boolean.TRUE.equals(request.getSentEmail())) {
            sendEmail(notification);
            notification.setEmailSent(true);
            notification = notificationRepository.save(notification);
        }

        return toResponse(notification);
    }

    @Override
    public Page<NotificationResponse> getNotifications(int page, int size, String title) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Notification> result;
        if (title != null && !title.isBlank()) {
            result = notificationRepository.findByActiveTrueAndTitleContainingIgnoreCase(title, pageable);
        } else {
            result = notificationRepository.findByActiveTrue(pageable);
        }
        return result.map(this::toResponse);
    }

    @Override
    public NotificationResponse getNotificationById(UUID notificationId) {
        var notification = notificationRepository.findById(notificationId)
                .filter(Notification::isActive)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));
        return toResponse(notification);
    }

    @Override
    @Transactional
    public NotificationResponse updateNotification(UUID notificationId, NotificationUpdateRequest request) {
        var notification = notificationRepository.findById(notificationId)
                .filter(Notification::isActive)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));

        if (request.getTitle() != null) {
            notification.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            notification.setContent(request.getContent());
        }
        if (request.getExpiresAt() != null) {
            notification.setExpiresAt(request.getExpiresAt());
        }
        if (request.getTargetRole() != null || request.getTargetUserId() != null) {
            var targetRole = request.getTargetRole() != null ? request.getTargetRole() : notification.getTargetRole();
            var targetUserId = request.getTargetUserId() != null
                    ? request.getTargetUserId()
                    : (notification.getTargetUser() != null ? notification.getTargetUser().getId() : null);
            validateTarget(targetRole, targetUserId);
            notification.setTargetRole(targetRole);
            notification.setTargetUser(resolveTargetUser(targetUserId));
        }

        if (Boolean.TRUE.equals(request.getSentEmail()) && !Boolean.TRUE.equals(notification.getEmailSent())) {
            sendEmail(notification);
            notification.setEmailSent(true);
        }

        return toResponse(notificationRepository.save(notification));
    }

    @Override
    @Transactional
    public void softDeleteNotification(UUID notificationId) {
        var notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));
        notificationRepository.delete(notification);
    }

    @Override
    public Page<NotificationResponse> getMyNotifications(int page, int size) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        var currentUser = usersRepository.findByUsernameAndActiveTrue(username)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
        Pageable pageable = PageRequest.of(page, size);
        return notificationRepository.findMyNotifications(
                currentUser.getId(),
                currentUser.getRole(),
                LocalDateTime.now(),
                pageable
        ).map(this::toResponse);
    }

    private void sendEmail(Notification notification) {
        List<String> emails = new ArrayList<>();
        if (notification.getTargetUser() != null) {
            emails.add(notification.getTargetUser().getEmail());
        }
        if (notification.getTargetRole() != null) {
            emails.addAll(usersRepository.findByRoleAndActiveTrue(notification.getTargetRole())
                    .stream()
                    .map(user -> user.getEmail())
                    .toList());
        }
        sendGridEmailService.send(notification.getTitle(), notification.getContent(), emails);
    }

    private com.tqp.cms.entity.Users resolveTargetUser(UUID userId) {
        if (userId == null) {
            return null;
        }
        return usersRepository.findByIdAndActiveTrue(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    private void validateTarget(com.tqp.cms.entity.UserRole targetRole, UUID targetUserId) {
        if (targetRole == null && targetUserId == null) {
            throw new AppException(ErrorCode.NOTIFICATION_TARGET_REQUIRED);
        }
    }

    private NotificationResponse toResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .content(notification.getContent())
                .emailSent(notification.getEmailSent())
                .targetRole(notification.getTargetRole())
                .targetUserId(notification.getTargetUser() != null ? notification.getTargetUser().getId() : null)
                .targetUsername(notification.getTargetUser() != null ? notification.getTargetUser().getUsername() : null)
                .targetUserEmail(notification.getTargetUser() != null ? notification.getTargetUser().getEmail() : null)
                .expiresAt(notification.getExpiresAt())
                .active(notification.isActive())
                .createdAt(notification.getCreatedAt())
                .updatedAt(notification.getUpdatedAt())
                .build();
    }
}
