package com.tqp.cms.controller;

import com.tqp.cms.dto.request.DoctorCreationRequest;
import com.tqp.cms.dto.request.DoctorUpdateRequest;
import com.tqp.cms.dto.response.ApiResponse;
import com.tqp.cms.dto.response.DoctorResponse;
import com.tqp.cms.service.DoctorService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/doctors")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@PreAuthorize("hasRole('ADMIN')")
public class DoctorController {
    DoctorService doctorService;

    @PostMapping
    public ResponseEntity<ApiResponse<DoctorResponse>> createDoctor(@RequestBody @Valid DoctorCreationRequest request) {
        DoctorResponse result = doctorService.createDoctor(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<DoctorResponse>builder()
                        .code(HttpStatus.CREATED.value())
                        .message("Doctor created successfully")
                        .result(result)
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<DoctorResponse>>> getDoctors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword
    ) {
        return ResponseEntity.ok(
                ApiResponse.<Page<DoctorResponse>>builder()
                        .code(HttpStatus.OK.value())
                        .message("Get doctors successfully")
                        .result(doctorService.getDoctors(page, size, keyword))
                        .build()
        );
    }

    @GetMapping("/{doctorId}")
    public ResponseEntity<ApiResponse<DoctorResponse>> getDoctorById(@PathVariable UUID doctorId) {
        return ResponseEntity.ok(
                ApiResponse.<DoctorResponse>builder()
                        .code(HttpStatus.OK.value())
                        .message("Get doctor successfully")
                        .result(doctorService.getDoctorById(doctorId))
                        .build()
        );
    }

    @PatchMapping("/{doctorId}")
    public ResponseEntity<ApiResponse<DoctorResponse>> updateDoctor(
            @PathVariable UUID doctorId,
            @RequestBody @Valid DoctorUpdateRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.<DoctorResponse>builder()
                        .code(HttpStatus.OK.value())
                        .message("Doctor updated successfully")
                        .result(doctorService.updateDoctor(doctorId, request))
                        .build()
        );
    }

    @DeleteMapping("/{doctorId}")
    public ResponseEntity<Void> softDeleteDoctor(@PathVariable UUID doctorId) {
        doctorService.softDeleteDoctor(doctorId);
        return ResponseEntity.noContent().build();
    }
}
