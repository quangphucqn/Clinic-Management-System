package com.tqp.cms.mapper;

import com.tqp.cms.dto.request.TimeSlotCreationRequest;
import com.tqp.cms.dto.response.TimeSlotResponse;
import com.tqp.cms.entity.TimeSlotConfig;
import org.springframework.stereotype.Component;

@Component
public class TimeSlotMapper {
    public TimeSlotConfig toEntity(TimeSlotCreationRequest request) {
        return TimeSlotConfig.builder()
                .slotCode(request.getSlotCode())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .maxPatientsPerSlot(request.getMaxPatientsPerSlot())
                .enabled(request.getEnabled() == null || request.getEnabled())
                .build();
    }

    public TimeSlotResponse toResponse(TimeSlotConfig entity) {
        return TimeSlotResponse.builder()
                .id(entity.getId())
                .slotCode(entity.getSlotCode())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .maxPatientsPerSlot(entity.getMaxPatientsPerSlot())
                .enabled(entity.isEnabled())
                .build();
    }
}
