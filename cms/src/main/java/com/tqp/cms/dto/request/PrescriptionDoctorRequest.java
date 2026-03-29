package com.tqp.cms.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PrescriptionDoctorRequest {
    @NotNull(message = "FIELD_REQUIRED")
    UUID medicalRecordId;
    String instructions;
    List<PrescriptionItemDoctorRequest> items;
}
