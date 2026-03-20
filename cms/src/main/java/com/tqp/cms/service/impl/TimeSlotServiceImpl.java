package com.tqp.cms.service.impl;

import com.tqp.cms.dto.request.TimeSlotCreationRequest;
import com.tqp.cms.dto.request.TimeSlotUpdateRequest;
import com.tqp.cms.dto.response.TimeSlotResponse;
import com.tqp.cms.exception.AppException;
import com.tqp.cms.exception.ErrorCode;
import com.tqp.cms.mapper.TimeSlotMapper;
import com.tqp.cms.repository.TimeSlotConfigRepository;
import com.tqp.cms.service.TimeSlotService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TimeSlotServiceImpl implements TimeSlotService {
    TimeSlotConfigRepository timeSlotConfigRepository;
    TimeSlotMapper timeSlotMapper;

    @Override
    @Transactional
    public TimeSlotResponse createTimeSlot(TimeSlotCreationRequest request) {
        if (timeSlotConfigRepository.existsBySlotCode(request.getSlotCode())) {
            throw new AppException(ErrorCode.SLOT_EXISTED);
        }
        validateTimeRange(request.getStartTime(), request.getEndTime());
        var saved = timeSlotConfigRepository.save(timeSlotMapper.toEntity(request));
        return timeSlotMapper.toResponse(saved);
    }

    @Override
    public TimeSlotResponse getTimeSlotById(UUID timeSlotId) {
        var entity = timeSlotConfigRepository.findById(timeSlotId)
                .filter(slot -> slot.isActive())
                .orElseThrow(() -> new AppException(ErrorCode.SLOT_NOT_FOUND));
        return timeSlotMapper.toResponse(entity);
    }

    @Override
    public Page<TimeSlotResponse> getTimeSlots(int page, int size, String slotCode) {
        Pageable pageable = PageRequest.of(page, size);
        Page<com.tqp.cms.entity.TimeSlotConfig> slotPage;
        if (slotCode != null && !slotCode.isBlank()) {
            slotPage = timeSlotConfigRepository.findByActiveTrueAndSlotCodeContainingIgnoreCase(slotCode, pageable);
        } else {
            slotPage = timeSlotConfigRepository.findByActiveTrue(pageable);
        }
        return slotPage.map(timeSlotMapper::toResponse);
    }

    @Override
    @Transactional
    public TimeSlotResponse updateTimeSlot(UUID timeSlotId, TimeSlotUpdateRequest request) {
        var entity = timeSlotConfigRepository.findById(timeSlotId)
                .filter(slot -> slot.isActive())
                .orElseThrow(() -> new AppException(ErrorCode.SLOT_NOT_FOUND));

        var startTime = request.getStartTime() != null ? request.getStartTime() : entity.getStartTime();
        var endTime = request.getEndTime() != null ? request.getEndTime() : entity.getEndTime();
        validateTimeRange(startTime, endTime);

        if (request.getStartTime() != null) {
            entity.setStartTime(request.getStartTime());
        }
        if (request.getEndTime() != null) {
            entity.setEndTime(request.getEndTime());
        }
        if (request.getMaxPatientsPerSlot() != null) {
            entity.setMaxPatientsPerSlot(request.getMaxPatientsPerSlot());
        }
        if (request.getEnabled() != null) {
            entity.setEnabled(request.getEnabled());
        }

        return timeSlotMapper.toResponse(timeSlotConfigRepository.save(entity));
    }

    @Override
    @Transactional
    public void softDeleteTimeSlot(UUID timeSlotId) {
        var entity = timeSlotConfigRepository.findById(timeSlotId)
                .orElseThrow(() -> new AppException(ErrorCode.SLOT_NOT_FOUND));
        timeSlotConfigRepository.delete(entity);
    }

    private void validateTimeRange(java.time.LocalTime startTime, java.time.LocalTime endTime) {
        if (startTime == null || endTime == null || !startTime.isBefore(endTime)) {
            throw new AppException(ErrorCode.SLOT_TIME_INVALID);
        }
    }
}
