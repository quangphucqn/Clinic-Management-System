package com.tqp.cms.dto.request;

import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TimeSlotUpdateRequest {
    LocalTime startTime;
    LocalTime endTime;

    @Positive(message = "VALIDATION_ERROR")
    Integer maxPatientsPerSlot;

    Boolean enabled;
}
