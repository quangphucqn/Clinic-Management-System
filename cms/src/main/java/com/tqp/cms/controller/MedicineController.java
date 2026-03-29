package com.tqp.cms.controller;

import com.tqp.cms.dto.request.MedicineCreationRequest;
import com.tqp.cms.dto.request.MedicineUpdateRequest;
import com.tqp.cms.dto.response.ApiResponse;
import com.tqp.cms.dto.response.MedicineImageResponse;
import com.tqp.cms.dto.response.MedicineResponse;
import com.tqp.cms.exception.ErrorCode;
import com.tqp.cms.service.MedicineService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/medicines")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@PreAuthorize("hasRole('ADMIN')")
public class MedicineController {
    MedicineService medicineService;

    @PostMapping
    public ResponseEntity<ApiResponse<MedicineResponse>> createMedicine(@RequestBody @Valid MedicineCreationRequest request) {
        MedicineResponse result = medicineService.createMedicine(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<MedicineResponse>builder()
                        .code(HttpStatus.CREATED.value())
                        .message("Medicine created successfully")
                        .result(result)
                        .build()
        );
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<MedicineResponse>> createMedicineWithOptionalImage(
            @RequestPart("data") @Valid MedicineCreationRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        MedicineResponse result = medicineService.createMedicine(request, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<MedicineResponse>builder()
                        .code(HttpStatus.CREATED.value())
                        .message("Medicine created successfully")
                        .result(result)
                        .build()
        );
    }

    @GetMapping
    public ApiResponse<Page<MedicineResponse>> getMedicines(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name
    ) {
        return ApiResponse.<Page<MedicineResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Get medicines successfully")
                .result(medicineService.getMedicines(page, size, name))
                .build();
    }

    @GetMapping("/{medicineId}")
    public ApiResponse<MedicineResponse> getMedicineById(@PathVariable UUID medicineId) {
        return ApiResponse.<MedicineResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Get medicine successfully")
                .result(medicineService.getMedicineById(medicineId))
                .build();
    }

    @PatchMapping("/{medicineId}")
    public ApiResponse<MedicineResponse> updateMedicine(
            @PathVariable UUID medicineId,
            @RequestBody @Valid MedicineUpdateRequest request
    ) {
        return ApiResponse.<MedicineResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Medicine updated successfully")
                .result(medicineService.updateMedicine(medicineId, request))
                .build();
    }

    @PatchMapping(value = "/{medicineId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<MedicineResponse> updateMedicineWithOptionalImage(
            @PathVariable UUID medicineId,
            @RequestPart("data") @Valid MedicineUpdateRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        return ApiResponse.<MedicineResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Medicine updated successfully")
                .result(medicineService.updateMedicine(medicineId, request, file))
                .build();
    }

    @DeleteMapping("/{medicineId}")
    public ResponseEntity<ApiResponse<Void>> softDeleteMedicine(@PathVariable UUID medicineId) {
        medicineService.softDeleteMedicine(medicineId);
        return ResponseEntity.status(ErrorCode.DELETED.getHttpStatus()).body(
                ApiResponse.<Void>builder()
                        .code(ErrorCode.DELETED.getCode())
                        .message(ErrorCode.DELETED.getMessage())
                        .build()
        );
    }

}
