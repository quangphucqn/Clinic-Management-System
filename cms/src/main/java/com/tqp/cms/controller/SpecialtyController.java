package com.tqp.cms.controller;

import com.tqp.cms.dto.request.SpecialtyCreationRequest;
import com.tqp.cms.dto.request.SpecialtyUpdateRequest;
import com.tqp.cms.dto.response.ApiResponse;
import com.tqp.cms.dto.response.SpecialtyResponse;
import com.tqp.cms.service.SpecialtyService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/specialties")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SpecialtyController {
    SpecialtyService specialtyService;

    @PostMapping
    public ResponseEntity<ApiResponse<SpecialtyResponse>> createSpecialty(
            @RequestBody @Valid SpecialtyCreationRequest request
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<SpecialtyResponse>builder()
                        .code(HttpStatus.OK.value())
                        .message("Specialty created successfully")
                        .result(specialtyService.createSpecialty(request))
                        .build()
        );
    }

    @GetMapping
    public ApiResponse<Page<SpecialtyResponse>> getSpecialties(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name
    ) {
        return ApiResponse.<Page<SpecialtyResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Get specialties successfully")
                .result(specialtyService.getSpecialties(page, size, name))
                .build();
    }

    @GetMapping("/{specialtyId}")
    public ApiResponse<SpecialtyResponse> getSpecialtyById(@PathVariable UUID specialtyId) {
        return ApiResponse.<SpecialtyResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Get specialty successfully")
                .result(specialtyService.getSpecialtyById(specialtyId))
                .build();
    }

    @PatchMapping("/{specialtyId}")
    public ApiResponse<SpecialtyResponse> updateSpecialty(
            @PathVariable UUID specialtyId,
            @RequestBody @Valid SpecialtyUpdateRequest request
    ) {
        return ApiResponse.<SpecialtyResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Specialty updated successfully")
                .result(specialtyService.updateSpecialty(specialtyId, request))
                .build();
    }

    @DeleteMapping("/{specialtyId}")
    public ResponseEntity<ApiResponse<Void>> softDeleteSpecialty(@PathVariable UUID specialtyId) {
        specialtyService.softDeleteSpecialty(specialtyId);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<Void>builder()
                        .code(HttpStatus.OK.value())
                        .message("Deleted")
                        .build()
        );
    }
}
