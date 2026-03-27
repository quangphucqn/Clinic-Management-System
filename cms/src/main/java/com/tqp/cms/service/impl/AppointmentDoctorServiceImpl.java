package com.tqp.cms.service.impl;

import com.tqp.cms.dto.request.AppointmentDoctorRequest;
import com.tqp.cms.dto.response.AppointmentDoctorResponse;
import com.tqp.cms.dto.response.PatientAppoinmentDoctorResponse;
import com.tqp.cms.entity.Appointment;
import com.tqp.cms.exception.AppException;
import com.tqp.cms.exception.ErrorCode;
import com.tqp.cms.repository.AppointmentRepository;
import com.tqp.cms.repository.UsersRepository;
import com.tqp.cms.service.AppointmentDoctorService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AppointmentDoctorServiceImpl implements AppointmentDoctorService {

    AppointmentRepository appointmentRepository;
    UsersRepository usersRepository;

    @Override
    @PreAuthorize("hasRole('DOCTOR')")
    public List<AppointmentDoctorResponse> getMyAppointments(AppointmentDoctorRequest request) {

        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        var user= usersRepository.findByUsername(name).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        var doctor = user.getDoctorProfile();


//        List<Appointment> appointments =
//                appointmentRepository.findByDoctorIdAndAppointmentDateAndStatus(
//                        doctor.getId(),
//                        request.getAppointmentDate(),
//                        request.getStatus()
//                );

        List<Appointment> appointments;

        if (request.getAppointmentDate() != null && request.getStatus() != null) {
            appointments = appointmentRepository
                    .findByDoctorIdAndAppointmentDateAndStatus(
                            doctor.getId(),
                            request.getAppointmentDate(),
                            request.getStatus()
                    );

        } else if (request.getAppointmentDate() != null) {
            appointments = appointmentRepository
                    .findByDoctorIdAndAppointmentDate(
                            doctor.getId(),
                            request.getAppointmentDate()
                    );
        } else if (request.getStatus() != null) {
            appointments = appointmentRepository
                    .findByDoctorIdAndStatus(
                            doctor.getId(),
                            request.getStatus()
                    );
        } else {
            appointments = appointmentRepository
                    .findByDoctorId(doctor.getId());
        }



        return appointments.stream()
                .map(this::mapToResponse)
                .toList();
    }

//    @PostAuthorize("returnObject.username == authentication.name")


    private AppointmentDoctorResponse mapToResponse(Appointment appointment) {
        return AppointmentDoctorResponse.builder()
                .id(appointment.getId())
                .patient(
                        PatientAppoinmentDoctorResponse.builder()
                                .fullName(appointment.getPatient().getUserAccount().getFullName())
                                .numberPhone(appointment.getPatient().getUserAccount().getEmail())
                                .build()
                )
                .appointmentDate(appointment.getAppointmentDate())
                .status(appointment.getStatus())
                .build();
    }
}