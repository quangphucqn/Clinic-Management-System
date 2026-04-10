package com.tqp.cms.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
public class DoctorReviewCreationRequest {
    @NotNull(message = "FIELD_REQUIRED")
    UUID appointmentId;

    @NotNull(message = "FIELD_REQUIRED")
    @Min(value = 1, message = "BAD_REQUEST")
    @Max(value = 5, message = "BAD_REQUEST")
    Integer rating;

    String comment;
}
