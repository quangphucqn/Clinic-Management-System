package com.tqp.cms.service;

import com.tqp.cms.dto.request.AppointmentBookingRequest;
import com.tqp.cms.dto.response.AppointmentBookingResponse;
import com.tqp.cms.dto.response.AppointmentHistoryResponse;
import com.tqp.cms.dto.response.PageResponse;
import com.tqp.cms.entity.AppointmentStatus;

public interface AppointmentPatientService {
    AppointmentBookingResponse bookAppointment(AppointmentBookingRequest request);

    PageResponse<AppointmentHistoryResponse> getMyAppointmentHistory(AppointmentStatus status, int page, int size);
}
