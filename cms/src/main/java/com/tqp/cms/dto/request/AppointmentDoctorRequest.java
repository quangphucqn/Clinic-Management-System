package com.tqp.cms.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tqp.cms.entity.AppointmentStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AppointmentDoctorRequest {

    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate appointmentDate;

    AppointmentStatus status;

    String patientName;


    Integer page = 0;
    Integer size = 1;
}
