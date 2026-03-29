package com.tqp.cms.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MedicalRecordDetailResponse {
    UUID id;
    String patientName;
    String diagnosis;
    String symptoms;
    String conclusion;
    LocalDateTime visitedAt;

    UUID prescriptionId;
    List<UUID> labTestOrderIds;
}
