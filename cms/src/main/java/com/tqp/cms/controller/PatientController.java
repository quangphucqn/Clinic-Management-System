package com.tqp.cms.controller;

import com.tqp.cms.dto.request.PatientRegistrationRequest;
import com.tqp.cms.dto.response.ApiResponse;
import com.tqp.cms.dto.response.PatientRegistrationResponse;
import com.tqp.cms.service.PatientService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PatientController {
    PatientService patientService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<PatientRegistrationResponse>> registerPatient(
            @RequestBody @Valid PatientRegistrationRequest request
    ) {
        PatientRegistrationResponse result = patientService.registerPatient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<PatientRegistrationResponse>builder()
                        .code(HttpStatus.CREATED.value())
                        .message("Patient account registered successfully")
                        .result(result)
                        .build()
        );
    }
}
