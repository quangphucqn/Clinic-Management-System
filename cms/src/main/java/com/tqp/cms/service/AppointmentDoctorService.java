package com.tqp.cms.service;

import com.tqp.cms.dto.request.AppointmentDoctorRequest;
import com.tqp.cms.dto.request.MedicineCreationRequest;
import com.tqp.cms.dto.response.AppointmentDoctorResponse;
import com.tqp.cms.dto.response.MedicineResponse;

import java.util.List;

public interface AppointmentDoctorService {
    List<AppointmentDoctorResponse> getMyAppointments(AppointmentDoctorRequest request);
}
