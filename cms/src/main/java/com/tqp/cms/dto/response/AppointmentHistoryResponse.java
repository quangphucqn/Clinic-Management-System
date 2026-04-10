package com.tqp.cms.dto.response;

import com.tqp.cms.entity.AppointmentStatus;
import java.math.BigDecimal;
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
public class AppointmentHistoryResponse {
    UUID appointmentId;
    UUID doctorId;
    String doctorName;
    UUID timeSlotId;
    String timeSlot;
    LocalDate appointmentDate;
    AppointmentStatus status;
    BigDecimal depositAmount;
    String reason;
    String note;
}
