package com.tqp.cms.controller;

import com.tqp.cms.dto.request.*;
import com.tqp.cms.dto.response.*;
import com.tqp.cms.service.*;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@Slf4j
@RestController
@RequestMapping("/doctor")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AppointmentDoctorController {

    AppointmentDoctorService appointmentDoctorService;
    MedicalRecordService medicalRecordService;
    PrescriptionDoctorService prescriptionDoctorService;
    LabTestOrderService labTestOrderService;
    LabTestResultService labTestResultService;
    MedicalHistoryService medicalHistoryService;


    @GetMapping("/appointments")
    public ApiResponse<PageResponse<AppointmentDoctorResponse>> getMyAppointments(
            @Valid @ModelAttribute AppointmentDoctorRequest request
    ) {
        return ApiResponse.<PageResponse<AppointmentDoctorResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Get Appointment successfully")
                .result(appointmentDoctorService.getMyAppointments(request))
                .build();
    }

    @GetMapping("appointments/{appointmentId}")
    public ApiResponse<AppointmentDoctorDetailsResponse> getAppointmentById(@PathVariable UUID appointmentId) {
        return ApiResponse.<AppointmentDoctorDetailsResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Get appointment successfully")
                .result(appointmentDoctorService.getAppointmentById(appointmentId))
                .build();
    }

    @PostMapping("/medical-records")
    @PreAuthorize("hasRole('DOCTOR')")
    public ApiResponse<?> createMedicalRecord(@RequestBody @Valid MedicalRecordRequest request) {
        return ApiResponse.<MedicalRecordResponse>builder()
                .code(HttpStatus.OK.value())
                .message("create medical record successfully")
                .result(medicalRecordService.createMedicalRecord(request))
                .build();
    }

    @GetMapping("/medical-records/{medicalRecordId}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ApiResponse<MedicalRecordDetailResponse> getMedicalRecord(@PathVariable UUID medicalRecordId) {
        return ApiResponse.<MedicalRecordDetailResponse>builder()
                .code(HttpStatus.OK.value())
                .message("get medical record successfully")
                .result(medicalRecordService.getMedicalRecordById(medicalRecordId))
                .build();
    }

    @PostMapping("/prescriptions")
    @PreAuthorize("hasRole('DOCTOR')")
    public ApiResponse<PrescriptionDoctorResponse> createPrescription(@RequestBody @Valid PrescriptionDoctorRequest request) {
        return ApiResponse.<PrescriptionDoctorResponse>builder()
                .code(HttpStatus.OK.value())
                .message("create prescription successfully")
                .result(prescriptionDoctorService.createPrescription(request))
                .build();
    }

    @GetMapping("/prescriptions/{prescriptionId}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ApiResponse<PrescriptionDoctorResponse> getPrescriptionById(@PathVariable UUID prescriptionId) {
        return ApiResponse.<PrescriptionDoctorResponse>builder()
                .code(HttpStatus.OK.value())
                .message("get prescription successfully")
                .result(prescriptionDoctorService.getPrescriptionById(prescriptionId))
                .build();
    }

    @PostMapping("/lab-tests")
    @PreAuthorize("hasRole('DOCTOR')")
    public ApiResponse<LabTestOrderResponse> createLabTestOrder(@RequestBody @Valid LabTestOrderRequest request) {
        return ApiResponse.<LabTestOrderResponse>builder()
                .code(HttpStatus.OK.value())
                .message("create lab test order successfully")
                .result(labTestOrderService.createLabTestOrder(request))
                .build();
    }

    @GetMapping("/lab-tests/{labTestOrderId}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ApiResponse<LabTestOrderDetailResponse> getLabTestOrderById(@PathVariable UUID labTestOrderId) {
        return ApiResponse.<LabTestOrderDetailResponse>builder()
                .code(HttpStatus.OK.value())
                .message("get lab test order successfully")
                .result(labTestOrderService.getLabTestOrderById(labTestOrderId))
                .build();
    }

    @PostMapping("/lab-results")
    public ApiResponse<?> createResult(@RequestBody LabTestResultRequest request) {
        return ApiResponse.builder()
                .code(HttpStatus.OK.value())
                .message("create lab test results successfully")
                .result(labTestResultService.createLabTestResult(request))
                .build();
    }

    @GetMapping("/patients/{patientId}/medical-history")
    @PreAuthorize("hasRole('DOCTOR')")
    public ApiResponse<PageResponse<MedicalHistoryResponse>> getHistory(@PathVariable UUID patientId,
                                                                @ModelAttribute MedicalHistoryRequest request) {
        return ApiResponse.<PageResponse<MedicalHistoryResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("get medical history successfully")
                .result(medicalHistoryService.getPatientHistory(patientId,request))
                .build();
    }
}
