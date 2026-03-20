package com.tqp.cms.controller;

import com.tqp.cms.dto.request.UnitCreationRequest;
import com.tqp.cms.dto.request.UnitUpdateRequest;
import com.tqp.cms.dto.response.ApiResponse;
import com.tqp.cms.dto.response.UnitResponse;
import com.tqp.cms.service.UnitService;
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
@RequestMapping("/units")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UnitController {
    UnitService unitService;

    @PostMapping
    public ResponseEntity<ApiResponse<UnitResponse>> createUnit(@RequestBody @Valid UnitCreationRequest request) {
        UnitResponse result = unitService.createUnit(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<UnitResponse>builder()
                        .code(HttpStatus.CREATED.value())
                        .message("Unit created successfully")
                        .result(result)
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<UnitResponse>>> getUnits(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name
    ) {
        return ResponseEntity.ok(
                ApiResponse.<Page<UnitResponse>>builder()
                        .code(HttpStatus.OK.value())
                        .message("Get units successfully")
                        .result(unitService.getUnits(page, size, name))
                        .build()
        );
    }

    @GetMapping("/{unitId}")
    public ResponseEntity<ApiResponse<UnitResponse>> getUnitById(@PathVariable UUID unitId) {
        return ResponseEntity.ok(
                ApiResponse.<UnitResponse>builder()
                        .code(HttpStatus.OK.value())
                        .message("Get unit successfully")
                        .result(unitService.getUnitById(unitId))
                        .build()
        );
    }

    @PatchMapping("/{unitId}")
    public ResponseEntity<ApiResponse<UnitResponse>> updateUnit(
            @PathVariable UUID unitId,
            @RequestBody @Valid UnitUpdateRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.<UnitResponse>builder()
                        .code(HttpStatus.OK.value())
                        .message("Unit updated successfully")
                        .result(unitService.updateUnit(unitId, request))
                        .build()
        );
    }

    @DeleteMapping("/{unitId}")
    public ResponseEntity<Void> softDeleteUnit(@PathVariable UUID unitId) {
        unitService.softDeleteUnit(unitId);
        return ResponseEntity.noContent().build();
    }
}
