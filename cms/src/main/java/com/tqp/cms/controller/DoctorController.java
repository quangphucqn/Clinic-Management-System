package com.tqp.cms.controller;

import com.tqp.cms.dto.request.DoctorCreationRequest;
import com.tqp.cms.dto.request.DoctorSelfUpdateRequest;
import com.tqp.cms.dto.request.DoctorUpdateRequest;
import com.tqp.cms.dto.response.ApiResponse;
import com.tqp.cms.dto.response.DoctorDetailResponse;
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

import java.util.UUID;

@RestController
@RequestMapping("/doctors")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DoctorController {
    DoctorService doctorService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DoctorDetailResponse>> createDoctor(@RequestBody @Valid DoctorCreationRequest request) {
        DoctorDetailResponse result = doctorService.createDoctor(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<DoctorDetailResponse>builder()
                        .code(HttpStatus.CREATED.value())
                        .message("Doctor created successfully")
                        .result(result)
                        .build()
        );
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<DoctorResponse>>> getDoctors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) UUID specialtyId
    ) {
        return ResponseEntity.ok(
                ApiResponse.<Page<DoctorResponse>>builder()
                        .code(HttpStatus.OK.value())
                        .message("Get doctors successfully")
                        .result(doctorService.getDoctors(page, size, keyword, specialtyId))
                        .build()
        );
    }

    @GetMapping("/{doctorId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DoctorDetailResponse>> getDoctorById(@PathVariable UUID doctorId) {
        return ResponseEntity.ok(
                ApiResponse.<DoctorDetailResponse>builder()
                        .code(HttpStatus.OK.value())
                        .message("Get doctor successfully")
                        .result(doctorService.getDoctorById(doctorId))
                        .build()
        );
    }

    @PatchMapping("/{doctorId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DoctorDetailResponse>> updateDoctor(
            @PathVariable UUID doctorId,
            @RequestBody @Valid DoctorUpdateRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.<DoctorDetailResponse>builder()
                        .code(HttpStatus.OK.value())
                        .message("Doctor updated successfully")
                        .result(doctorService.updateDoctor(doctorId, request))
                        .build()
        );
    }

    @DeleteMapping("/{doctorId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> softDeleteDoctor(@PathVariable UUID doctorId) {
        doctorService.softDeleteDoctor(doctorId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/me/profile")
    @PreAuthorize("hasRole('DOCTOR')")
    public ApiResponse<DoctorDetailResponse> updateMyProfile(@RequestBody @Valid DoctorSelfUpdateRequest request) {
        return ApiResponse.<DoctorDetailResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Doctor profile updated successfully")
                .result(doctorService.updateMyProfile(request))
                .build();
    }
}
