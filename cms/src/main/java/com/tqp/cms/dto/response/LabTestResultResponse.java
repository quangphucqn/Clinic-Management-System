package com.tqp.cms.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LabTestResultResponse {
    String resultValue;
    String normalRange;
    String attachmentUrl;
}
