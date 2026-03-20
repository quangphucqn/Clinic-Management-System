package com.tqp.cms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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
public class TimeSlotCreationRequest {
    @NotBlank(message = "FIELD_REQUIRED")
    @Size(max = 30, message = "VALIDATION_ERROR")
    String slotCode;

    @NotNull(message = "FIELD_REQUIRED")
    LocalTime startTime;

    @NotNull(message = "FIELD_REQUIRED")
    LocalTime endTime;

    @NotNull(message = "FIELD_REQUIRED")
    @Positive(message = "VALIDATION_ERROR")
    Integer maxPatientsPerSlot;

    Boolean enabled;
}
