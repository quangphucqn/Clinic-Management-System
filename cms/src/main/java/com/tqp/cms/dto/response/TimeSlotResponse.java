package com.tqp.cms.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TimeSlotResponse {
    UUID id;
    String slotCode;
    LocalTime startTime;
    LocalTime endTime;
    Integer maxPatientsPerSlot;
    boolean enabled;
    boolean active;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
