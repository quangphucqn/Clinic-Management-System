package com.tqp.cms.dto.response;

import com.tqp.cms.entity.AppointmentStatus;
import com.tqp.cms.entity.Doctor;
import com.tqp.cms.entity.Patient;
import com.tqp.cms.entity.TimeSlotConfig;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AppointmentDoctorDetailsResponse {
    UUID id;
    PatientAppoinmentDoctorResponse patient;
    Doctor doctor;
    TimeSlotConfig timeSlotConfig;
    LocalDate appointmentDate;
    AppointmentStatus status;
    BigDecimal depositAmount;
    String reason;
    String note;
    LocalDateTime createdAt;
}
