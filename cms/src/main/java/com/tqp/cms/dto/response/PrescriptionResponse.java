package com.tqp.cms.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PrescriptionResponse {
    UUID id;
    UUID medicalRecordId;
    UUID patientId;
    String patientName;
    UUID doctorId;
    String doctorName;
    String instructions;
    LocalDateTime issuedAt;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    List<PrescriptionItemResponse> items;
}
