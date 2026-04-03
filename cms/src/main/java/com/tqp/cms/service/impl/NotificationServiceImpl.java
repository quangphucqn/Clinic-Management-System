package com.tqp.cms.service.impl;

import com.tqp.cms.dto.request.NotificationCreationRequest;
import com.tqp.cms.dto.request.NotificationUpdateRequest;
import com.tqp.cms.dto.response.NotificationListResponse;
import com.tqp.cms.dto.response.NotificationResponse;
import com.tqp.cms.entity.Notification;
import com.tqp.cms.entity.UserRole;
import com.tqp.cms.exception.AppException;
import com.tqp.cms.exception.ErrorCode;
import com.tqp.cms.mapper.NotificationMapper;
import com.tqp.cms.repository.NotificationRepository;
import com.tqp.cms.repository.UsersRepository;
import com.tqp.cms.service.NotificationService;
import com.tqp.cms.service.NotificationRealtimePublisher;
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
import org.springframework.data.domain.Sort;
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
    NotificationRealtimePublisher notificationRealtimePublisher;
    NotificationMapper notificationMapper;

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
                .emailSent(false)
                .build();

        notification = notificationRepository.save(notification);

        if (Boolean.TRUE.equals(request.getSentEmail())) {
            sendEmail(notification);
            notification.setEmailSent(true);
            notification = notificationRepository.save(notification);
        }

        notificationRealtimePublisher.publish(notification);

        return notificationMapper.toAdminDetailResponse(notification);
    }

    @Override
    public Page<NotificationListResponse> getNotifications(int page, int size, String title) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Notification> result;
        if (title != null && !title.isBlank()) {
            result = notificationRepository.findByActiveTrueAndTitleContainingIgnoreCase(title, pageable);
        } else {
            result = notificationRepository.findByActiveTrue(pageable);
        }
        return result.map(notificationMapper::toListResponse);
    }

    @Override
    public NotificationResponse getNotificationById(UUID notificationId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        var currentUser = usersRepository.findByUsernameAndActiveTrue(username)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        var notification = notificationRepository.findById(notificationId)
                .filter(Notification::isActive)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));

        if (currentUser.getRole() == UserRole.ADMIN) {
            return notificationMapper.toAdminDetailResponse(notification);
        }

        boolean visibleByUser = notification.getTargetUser() != null
                && notification.getTargetUser().getId().equals(currentUser.getId());
        boolean visibleByRole = notification.getTargetRole() != null
                && notification.getTargetRole() == currentUser.getRole();
        if (!visibleByUser && !visibleByRole) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        return notificationMapper.toNonAdminDetailResponse(notification);
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

        return notificationMapper.toAdminDetailResponse(notificationRepository.save(notification));
    }

    @Override
    @Transactional
    public void softDeleteNotification(UUID notificationId) {
        var notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));
        notificationRepository.delete(notification);
    }

    @Override
    public Page<NotificationListResponse> getMyNotifications(int page, int size) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        var currentUser = usersRepository.findByUsernameAndActiveTrue(username)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return notificationRepository.findMyNotifications(
                currentUser.getId(),
                currentUser.getRole(),
                LocalDateTime.now(),
                pageable
        ).map(notificationMapper::toListResponse);
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

}
