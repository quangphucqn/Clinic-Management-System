package com.tqp.cms.dto.response;

import com.tqp.cms.entity.AppointmentStatus;
import com.tqp.cms.entity.Doctor;
import com.tqp.cms.entity.Patient;
import com.tqp.cms.entity.TimeSlotConfig;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AppointmentDoctorResponse {
        UUID id;
        PatientAppoinmentDoctorResponse patient;
        LocalDate appointmentDate;
        AppointmentStatus status;
}
