package com.tqp.cms.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PrescriptionItemDoctorRequest {
    @NotNull(message = "FIELD_REQUIRED")
    UUID medicineId;
    Integer quantity;
    String dosage;
    String frequency;
    Integer durationDays;
    String note;
}
