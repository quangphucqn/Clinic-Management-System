package com.tqp.cms.repository;

import com.tqp.cms.entity.TimeSlotConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TimeSlotConfigRepository extends JpaRepository<TimeSlotConfig, UUID> {
    boolean existsBySlotCode(String slotCode);

    Optional<TimeSlotConfig> findBySlotCode(String slotCode);

    Page<TimeSlotConfig> findByActiveTrue(Pageable pageable);

    Page<TimeSlotConfig> findByActiveTrueAndSlotCodeContainingIgnoreCase(String slotCode, Pageable pageable);
}
