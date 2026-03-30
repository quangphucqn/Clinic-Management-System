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
public class MedicalHistoryResponse {
    UUID medicalRecordId;
    LocalDateTime visitedAt;
    String diagnosis;
    String doctorName;
    UUID prescriptionId;
    List<UUID> labTestOrderIds;
}
