package com.tqp.cms.service.impl;

import com.tqp.cms.dto.request.AppointmentDoctorRequest;
import com.tqp.cms.dto.response.AppointmentDoctorResponse;
import com.tqp.cms.entity.Appointment;
import com.tqp.cms.repository.AppointmentRepository;
import com.tqp.cms.service.AppointmentDoctorService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AppointmentDoctorServiceImpl implements AppointmentDoctorService {

    AppointmentRepository appointmentRepository;

    @Override
    public List<AppointmentDoctorResponse> getMyAppointments(AppointmentDoctorRequest request) {

        List<Appointment> appointments =
                appointmentRepository.findByAppointmentDate(request.getAppointmentDate());

        return appointments.stream()
                .map(this::mapToResponse)
                .toList();
    }

    private AppointmentDoctorResponse mapToResponse(Appointment appointment) {
        return AppointmentDoctorResponse.builder()
                .id(appointment.getId())
                .appointmentDate(appointment.getAppointmentDate())
                .status(appointment.getStatus())
                .build();
    }
}