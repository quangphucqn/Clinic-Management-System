package com.tqp.cms.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tqp.cms.entity.AppointmentStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDoctorRequest {

    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate appointmentDate;

    AppointmentStatus status;


    Integer page = 0;
    Integer size = 1;
}
