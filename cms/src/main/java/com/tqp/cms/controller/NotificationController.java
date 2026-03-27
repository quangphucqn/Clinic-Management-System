package com.tqp.cms.controller;

import com.tqp.cms.dto.request.NotificationCreationRequest;
import com.tqp.cms.dto.request.NotificationUpdateRequest;
import com.tqp.cms.dto.response.ApiResponse;
import com.tqp.cms.dto.response.NotificationResponse;
import com.tqp.cms.service.NotificationService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationController {
    NotificationService notificationService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<NotificationResponse>> createNotification(
            @RequestBody @Valid NotificationCreationRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<NotificationResponse>builder()
                        .code(HttpStatus.CREATED.value())
                        .message("Notification created successfully")
                        .result(notificationService.createNotification(request))
                        .build()
        );
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Page<NotificationResponse>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String title
    ) {
        return ApiResponse.<Page<NotificationResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Get notifications successfully")
                .result(notificationService.getNotifications(page, size, title))
                .build();
    }

    @GetMapping("/{notificationId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<NotificationResponse> getNotificationById(@PathVariable UUID notificationId) {
        return ApiResponse.<NotificationResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Get notification successfully")
                .result(notificationService.getNotificationById(notificationId))
                .build();
    }

    @PatchMapping("/{notificationId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<NotificationResponse> updateNotification(
            @PathVariable UUID notificationId,
            @RequestBody @Valid NotificationUpdateRequest request
    ) {
        return ApiResponse.<NotificationResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Notification updated successfully")
                .result(notificationService.updateNotification(notificationId, request))
                .build();
    }

    @DeleteMapping("/{notificationId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> softDeleteNotification(@PathVariable UUID notificationId) {
        notificationService.softDeleteNotification(notificationId);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<Void>builder()
                        .code(HttpStatus.OK.value())
                        .message("Deleted")
                        .build()
        );
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<Page<NotificationResponse>> getMyNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.<Page<NotificationResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Get my notifications successfully")
                .result(notificationService.getMyNotifications(page, size))
                .build();
    }
}
