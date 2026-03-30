package com.tqp.cms.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LabTestOrderDetailResponse {
    UUID id;
    String testName;
    String status;
    String requestNote;

    LabTestResultResponse result;
}