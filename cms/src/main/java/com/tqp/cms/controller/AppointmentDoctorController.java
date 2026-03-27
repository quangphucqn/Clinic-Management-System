package com.tqp.cms.controller;

import com.tqp.cms.dto.request.AppointmentDoctorRequest;
import com.tqp.cms.dto.response.ApiResponse;
import com.tqp.cms.dto.response.AppointmentDoctorResponse;
import com.tqp.cms.dto.response.PrescriptionResponse;
import com.tqp.cms.service.AppointmentDoctorService;
import com.tqp.cms.service.PrescriptionService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@Slf4j
@RestController
@RequestMapping("/doctor/appointments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AppointmentDoctorController {

    AppointmentDoctorService appointmentDoctorService;

    @GetMapping
    public ApiResponse<List<AppointmentDoctorResponse>> getMyAppointments(
            @Valid @ModelAttribute AppointmentDoctorRequest request
    ) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("UserName: {}",authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));


        return ApiResponse.<List<AppointmentDoctorResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Get Appointment successfully")
                .result(appointmentDoctorService.getMyAppointments( request))
                .build();
    }
}
