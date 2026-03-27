package com.tqp.cms.service;

import com.tqp.cms.dto.request.AppointmentDoctorRequest;
import com.tqp.cms.dto.request.MedicineCreationRequest;
import com.tqp.cms.dto.response.AppointmentDoctorDetailsResponse;
import com.tqp.cms.dto.response.AppointmentDoctorResponse;
import com.tqp.cms.dto.response.MedicineResponse;
import com.tqp.cms.dto.response.PageResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface AppointmentDoctorService {
    PageResponse<AppointmentDoctorResponse> getMyAppointments(AppointmentDoctorRequest request);
    AppointmentDoctorDetailsResponse getAppointmentById(UUID appointmentId);
}
