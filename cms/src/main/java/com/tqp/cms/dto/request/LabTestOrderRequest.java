package com.tqp.cms.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LabTestOrderRequest {
    UUID medicalRecordId;
    String testName;
    String requestNote;
}
