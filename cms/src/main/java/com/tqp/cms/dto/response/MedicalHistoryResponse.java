package com.tqp.cms.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MedicalHistoryResponse {
    UUID medicalRecordId;
    LocalDate appointmentDate;
    String timeSlot;
    LocalDateTime visitedAt;
    String symptoms;
    String diagnosis;
    String conclusion;
    String doctorName;
    UUID prescriptionId;
    List<UUID> labTestOrderIds;
}
