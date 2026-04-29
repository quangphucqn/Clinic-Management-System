package com.tqp.cms.service.impl;

import com.tqp.cms.dto.request.AppointmentDoctorRequest;
import com.tqp.cms.dto.response.*;
import com.tqp.cms.entity.Appointment;
import com.tqp.cms.exception.AppException;
import com.tqp.cms.exception.ErrorCode;
import com.tqp.cms.repository.AppointmentRepository;
import com.tqp.cms.repository.DoctorRepository;
import com.tqp.cms.repository.UsersRepository;
import com.tqp.cms.service.AppointmentDoctorService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AppointmentDoctorServiceImpl implements AppointmentDoctorService {

    AppointmentRepository appointmentRepository;
    UsersRepository usersRepository;
    DoctorRepository doctorRepository;

    @Override
    @PreAuthorize("hasRole('DOCTOR')")
    public PageResponse<AppointmentDoctorResponse> getMyAppointments(AppointmentDoctorRequest request) {

        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        var user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        var doctor = doctorRepository.findByUserAccountId(user.getId())
                .filter(item -> item.isActive())
                .orElseThrow(() -> new AppException(ErrorCode.DOCTOR_NOT_FOUND));

        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getSize(),
                Sort.by("CreatedAt").descending()
        );

        Page<Appointment> appointments;

        if (request.getPatientName() != null
                && !request.getPatientName().isBlank()
                && request.getAppointmentDate() != null) {

            appointments = appointmentRepository
                    .findByDoctorIdAndPatient_UserAccount_FullNameContainingIgnoreCaseAndAppointmentDate(
                            doctor.getId(),
                            request.getPatientName(),
                            request.getAppointmentDate(),
                            pageable
                    );

        } else if (request.getAppointmentDate() != null
                && request.getStatus() != null) {

            appointments = appointmentRepository
                    .findByDoctorIdAndAppointmentDateAndStatus(
                            doctor.getId(),
                            request.getAppointmentDate(),
                            request.getStatus(),
                            pageable
                    );

        } else if (request.getAppointmentDate() != null) {

            appointments = appointmentRepository
                    .findByDoctorIdAndAppointmentDate(
                            doctor.getId(),
                            request.getAppointmentDate(),
                            pageable
                    );

        } else if (request.getStatus() != null) {

            appointments = appointmentRepository
                    .findByDoctorIdAndStatus(
                            doctor.getId(),
                            request.getStatus(),
                            pageable
                    );

        } else {

            appointments = appointmentRepository
                    .findByDoctorId(
                            doctor.getId(),
                            pageable
                    );
        }

        return PageResponse.<AppointmentDoctorResponse>builder()
                .content(appointments.getContent().stream().map(this::mapToResponse).toList())
                .page(appointments.getNumber())
                .size(appointments.getSize())
                .totalElements(appointments.getTotalElements())
                .totalPages(appointments.getTotalPages())
                .build();
    }

    private AppointmentDoctorResponse mapToResponse(Appointment appointment) {
        return AppointmentDoctorResponse.builder()
                .id(appointment.getId())
                .patient(
                        PatientAppoinmentDoctorResponse.builder()
                                .patientId(appointment.getPatient().getId())
                                .fullName(appointment.getPatient().getUserAccount().getFullName())
                                .email(appointment.getPatient().getUserAccount().getEmail())
                                .build()
                )
                .appointmentDate(appointment.getAppointmentDate())
                .status(appointment.getStatus())
                .build();
    }

    @Override
    @PreAuthorize("hasRole('DOCTOR')")
    public AppointmentDoctorDetailsResponse getAppointmentById(UUID appointmentId) {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        var user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        var doctor = doctorRepository.findByUserAccountId(user.getId())
                .filter(item -> item.isActive())
                .orElseThrow(() -> new AppException(ErrorCode.DOCTOR_NOT_FOUND));

        var appointment = appointmentRepository.findById(appointmentId)
                .filter(m -> m.isActive())
                .orElseThrow(() -> new AppException(ErrorCode.APPOINTMENT_NOT_FOUND));

        if (!appointment.getDoctor().getId().equals(doctor.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        return mapToDetailsResponse(appointment);
    }
    private AppointmentDoctorDetailsResponse mapToDetailsResponse(Appointment appointment) {
        return AppointmentDoctorDetailsResponse.builder()
                .id(appointment.getId())
                .patient(
                        PatientAppoinmentDoctorResponse.builder()
                                .patientId(appointment.getPatient().getId())
                                .fullName(appointment.getPatient().getUserAccount().getFullName())
                                .email(appointment.getPatient().getUserAccount().getEmail())
                                .build()
                )
                .medicalRecordID(appointment.getMedicalRecord() != null
                        ? appointment.getMedicalRecord().getId()
                        : null)
                .appointmentDate(appointment.getAppointmentDate())
                .timeSlot(appointment.getTimeSlotConfig().getStartTime() + " - " +
                        appointment.getTimeSlotConfig().getEndTime())
                .status(appointment.getStatus())
                .depositAmount(appointment.getDepositAmount())
                .reason(appointment.getReason())
                .note(appointment.getNote())
                .build();
    }

}