package com.tqp.cms.controller;

import com.tqp.cms.dto.request.TimeSlotCreationRequest;
import com.tqp.cms.dto.request.TimeSlotUpdateRequest;
import com.tqp.cms.dto.response.ApiResponse;
import com.tqp.cms.dto.response.TimeSlotResponse;
import com.tqp.cms.exception.ErrorCode;
import com.tqp.cms.service.TimeSlotService;
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
@RequestMapping("/time-slots")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@PreAuthorize("hasRole('ADMIN')")
public class TimeSlotController {
    TimeSlotService timeSlotService;

    @PostMapping
    public ResponseEntity<ApiResponse<TimeSlotResponse>> createTimeSlot(@RequestBody @Valid TimeSlotCreationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<TimeSlotResponse>builder()
                        .code(HttpStatus.CREATED.value())
                        .message("Time slot created successfully")
                        .result(timeSlotService.createTimeSlot(request))
                        .build()
        );
    }

    @GetMapping
    public ApiResponse<Page<TimeSlotResponse>> getTimeSlots(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String slotCode
    ) {
        return ApiResponse.<Page<TimeSlotResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Get time slots successfully")
                .result(timeSlotService.getTimeSlots(page, size, slotCode))
                .build();
    }

    @GetMapping("/{timeSlotId}")
    public ApiResponse<TimeSlotResponse> getTimeSlotById(@PathVariable UUID timeSlotId) {
        return ApiResponse.<TimeSlotResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Get time slot successfully")
                .result(timeSlotService.getTimeSlotById(timeSlotId))
                .build();
    }

    @PatchMapping("/{timeSlotId}")
    public ApiResponse<TimeSlotResponse> updateTimeSlot(
            @PathVariable UUID timeSlotId,
            @RequestBody @Valid TimeSlotUpdateRequest request
    ) {
        return ApiResponse.<TimeSlotResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Time slot updated successfully")
                .result(timeSlotService.updateTimeSlot(timeSlotId, request))
                .build();
    }

    @DeleteMapping("/{timeSlotId}")
    public ResponseEntity<ApiResponse<Void>> softDeleteTimeSlot(@PathVariable UUID timeSlotId) {
        timeSlotService.softDeleteTimeSlot(timeSlotId);
        return ResponseEntity.status(ErrorCode.DELETED.getHttpStatus()).body(
                ApiResponse.<Void>builder()
                        .code(ErrorCode.DELETED.getCode())
                        .message(ErrorCode.DELETED.getMessage())
                        .build()
        );
    }
}
