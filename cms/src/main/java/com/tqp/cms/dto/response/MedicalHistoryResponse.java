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
    String doctorName;
    String diagnosis;
    LocalDateTime visitedAt;
    PrescriptionResponse prescription;
    List<LabTestOrderResponse> labTests;
}
