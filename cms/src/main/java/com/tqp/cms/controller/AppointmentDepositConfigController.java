package com.tqp.cms.controller;

import com.tqp.cms.dto.request.AppointmentDepositConfigUpdateRequest;
import com.tqp.cms.dto.response.ApiResponse;
import com.tqp.cms.dto.response.AppointmentDepositConfigResponse;
import com.tqp.cms.service.AppointmentDepositConfigService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/appointment-deposit-config")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AppointmentDepositConfigController {
    AppointmentDepositConfigService appointmentDepositConfigService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','PATIENT')")
    public ApiResponse<AppointmentDepositConfigResponse> getConfig() {
        return ApiResponse.<AppointmentDepositConfigResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Get appointment deposit config successfully")
                .result(appointmentDepositConfigService.getConfig())
                .build();
    }

    @PatchMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<AppointmentDepositConfigResponse> updateConfig(
            @RequestBody @Valid AppointmentDepositConfigUpdateRequest request
    ) {
        return ApiResponse.<AppointmentDepositConfigResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Appointment deposit config updated successfully")
                .result(appointmentDepositConfigService.updateConfig(request))
                .build();
    }
}
