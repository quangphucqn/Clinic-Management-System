package com.tqp.cms.service;

import com.tqp.cms.dto.request.TimeSlotCreationRequest;
import com.tqp.cms.dto.request.TimeSlotUpdateRequest;
import com.tqp.cms.dto.response.TimeSlotResponse;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface TimeSlotService {
    TimeSlotResponse createTimeSlot(TimeSlotCreationRequest request);

    TimeSlotResponse getTimeSlotById(UUID timeSlotId);

    Page<TimeSlotResponse> getTimeSlots(int page, int size, String slotCode);

    TimeSlotResponse updateTimeSlot(UUID timeSlotId, TimeSlotUpdateRequest request);

    void softDeleteTimeSlot(UUID timeSlotId);
}
