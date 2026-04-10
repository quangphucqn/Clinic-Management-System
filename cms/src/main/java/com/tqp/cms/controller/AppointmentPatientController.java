package com.tqp.cms.controller;

import com.tqp.cms.dto.request.AppointmentBookingRequest;
import com.tqp.cms.dto.request.DoctorReviewCreationRequest;
import com.tqp.cms.dto.response.ApiResponse;
import com.tqp.cms.dto.response.AppointmentBookingResponse;
import com.tqp.cms.dto.response.AppointmentHistoryResponse;
import com.tqp.cms.dto.response.DoctorReviewResponse;
import com.tqp.cms.dto.response.PatientLabResultResponse;
import com.tqp.cms.dto.response.PageResponse;
import com.tqp.cms.dto.response.PrescriptionResponse;
import com.tqp.cms.entity.AppointmentStatus;
import com.tqp.cms.service.AppointmentPatientService;
import com.tqp.cms.service.DoctorReviewService;
import com.tqp.cms.service.LabTestResultService;
import com.tqp.cms.service.PrescriptionService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/patient")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AppointmentPatientController {
    AppointmentPatientService appointmentPatientService;
    LabTestResultService labTestResultService;
    DoctorReviewService doctorReviewService;
    PrescriptionService prescriptionService;

    @PostMapping("/appointments")
    public ResponseEntity<ApiResponse<AppointmentBookingResponse>> bookAppointment(
            @RequestBody @Valid AppointmentBookingRequest request
    ) {
        AppointmentBookingResponse result = appointmentPatientService.bookAppointment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<AppointmentBookingResponse>builder()
                        .code(HttpStatus.CREATED.value())
                        .message("Appointment booked successfully")
                        .result(result)
                        .build()
        );
    }

    @GetMapping("/appointments")
    public ApiResponse<PageResponse<AppointmentHistoryResponse>> getMyAppointmentHistory(
            @RequestParam(required = false) AppointmentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.<PageResponse<AppointmentHistoryResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Get appointment history successfully")
                .result(appointmentPatientService.getMyAppointmentHistory(status, page, size))
                .build();
    }

    @GetMapping("/lab-results")
    public ApiResponse<List<PatientLabResultResponse>> getMyLabResults() {
        return ApiResponse.<List<PatientLabResultResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Get lab results successfully")
                .result(labTestResultService.getMyLabResults())
                .build();
    }

    @GetMapping("/prescriptions")
    public ApiResponse<PageResponse<PrescriptionResponse>> getMyPrescriptions(
            @RequestParam(required = false) LocalDate issuedDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.<PageResponse<PrescriptionResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Get prescriptions successfully")
                .result(prescriptionService.getMyPrescriptions(issuedDate, page, size))
                .build();
    }

    @PostMapping("/reviews")
    public ResponseEntity<ApiResponse<DoctorReviewResponse>> createDoctorReview(
            @RequestBody @Valid DoctorReviewCreationRequest request
    ) {
        DoctorReviewResponse result = doctorReviewService.createReview(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<DoctorReviewResponse>builder()
                        .code(HttpStatus.CREATED.value())
                        .message("Doctor review created successfully")
                        .result(result)
                        .build()
        );
    }
}
