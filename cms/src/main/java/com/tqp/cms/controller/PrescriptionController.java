package com.tqp.cms.controller;

import com.tqp.cms.dto.response.ApiResponse;
import com.tqp.cms.dto.response.PrescriptionResponse;
import com.tqp.cms.service.PrescriptionService;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/prescriptions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PrescriptionController {
    PrescriptionService prescriptionService;

    @GetMapping("/{prescriptionId}")
    public ApiResponse<PrescriptionResponse> getPrescriptionById(@PathVariable UUID prescriptionId) {
        return ApiResponse.<PrescriptionResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Get prescription successfully")
                .result(prescriptionService.getPrescriptionById(prescriptionId))
                .build();
    }

    @GetMapping("/medical-record/{medicalRecordId}")
    public ApiResponse<PrescriptionResponse> getPrescriptionByMedicalRecordId(@PathVariable UUID medicalRecordId) {
        return ApiResponse.<PrescriptionResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Get prescription successfully")
                .result(prescriptionService.getPrescriptionByMedicalRecordId(medicalRecordId))
                .build();
    }
}
