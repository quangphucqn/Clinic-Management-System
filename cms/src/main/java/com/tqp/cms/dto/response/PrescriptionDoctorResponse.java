package com.tqp.cms.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PrescriptionDoctorResponse {
    UUID id;
    String patientName;
    List<PrescriptionItemDoctorResponse> items;
}
