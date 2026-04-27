package com.tqp.cms.controller;

import com.tqp.cms.dto.response.ApiResponse;
import com.tqp.cms.dto.response.PatientMonthlyStatisticResponse;
import com.tqp.cms.dto.response.PatientQuarterlyStatisticResponse;
import com.tqp.cms.dto.response.PatientYearlyStatisticResponse;
import com.tqp.cms.service.PatientStatisticsService;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/statistics/patients")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@PreAuthorize("hasRole('ADMIN')")
public class PatientStatisticsController {
    PatientStatisticsService patientStatisticsService;

    @GetMapping("/monthly")
    public ApiResponse<List<PatientMonthlyStatisticResponse>> getPatientStatsByMonth() {
        return ApiResponse.<List<PatientMonthlyStatisticResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Get patient statistics by month successfully")
                .result(patientStatisticsService.getPatientStatisticsByMonth())
                .build();
    }

    @GetMapping("/quarterly")
    public ApiResponse<List<PatientQuarterlyStatisticResponse>> getPatientStatsByQuarter() {
        return ApiResponse.<List<PatientQuarterlyStatisticResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Get patient statistics by quarter successfully")
                .result(patientStatisticsService.getPatientStatisticsByQuarter())
                .build();
    }

    @GetMapping("/yearly")
    public ApiResponse<List<PatientYearlyStatisticResponse>> getPatientStatsByYear() {
        return ApiResponse.<List<PatientYearlyStatisticResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Get patient statistics by year successfully")
                .result(patientStatisticsService.getPatientStatisticsByYear())
                .build();
    }
}
