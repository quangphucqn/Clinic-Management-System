package com.tqp.cms.repository;

import com.tqp.cms.entity.Notification;
import com.tqp.cms.entity.UserRole;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    Page<Notification> findByActiveTrue(Pageable pageable);

    Page<Notification> findByActiveTrueAndTitleContainingIgnoreCase(String title, Pageable pageable);

    @Query("""
            select n from Notification n
            where n.active = true
              and (n.expiresAt is null or n.expiresAt >= :now)
              and (n.targetUser.id = :userId or n.targetRole = :role)
            """)
    Page<Notification> findMyNotifications(
            @Param("userId") UUID userId,
            @Param("role") UserRole role,
            @Param("now") LocalDateTime now,
            Pageable pageable
    );
}
