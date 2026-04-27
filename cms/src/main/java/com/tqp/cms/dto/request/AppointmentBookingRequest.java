package com.tqp.cms.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tqp.cms.entity.PaymentMethod;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
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
public class AppointmentBookingRequest {
    @NotNull(message = "FIELD_REQUIRED")
    UUID doctorId;

    @NotNull(message = "FIELD_REQUIRED")
    UUID timeSlotId;

    @NotNull(message = "FIELD_REQUIRED")
    @FutureOrPresent(message = "BAD_REQUEST")
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate appointmentDate;

    @NotBlank(message = "FIELD_REQUIRED")
    String reason;

    String note;

    @NotNull(message = "FIELD_REQUIRED")
    PaymentMethod paymentMethod;

    String transactionCode;
}
