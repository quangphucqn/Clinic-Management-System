package com.tqp.cms.service.impl;

import com.tqp.cms.dto.request.AppointmentBookingRequest;
import com.tqp.cms.dto.response.AppointmentBookingResponse;
import com.tqp.cms.dto.response.AppointmentHistoryResponse;
import com.tqp.cms.dto.response.PageResponse;
import com.tqp.cms.entity.Appointment;
import com.tqp.cms.entity.AppointmentStatus;
import com.tqp.cms.exception.AppException;
import com.tqp.cms.exception.ErrorCode;
import com.tqp.cms.repository.AppointmentRepository;
import com.tqp.cms.repository.DoctorRepository;
import com.tqp.cms.repository.PatientRepository;
import com.tqp.cms.repository.TimeSlotConfigRepository;
import com.tqp.cms.repository.UsersRepository;
import com.tqp.cms.service.AppointmentPatientService;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AppointmentPatientServiceImpl implements AppointmentPatientService {
    AppointmentRepository appointmentRepository;
    UsersRepository usersRepository;
    PatientRepository patientRepository;
    DoctorRepository doctorRepository;
    TimeSlotConfigRepository timeSlotConfigRepository;

    @Override
    @Transactional
    @PreAuthorize("hasRole('PATIENT')")
    public AppointmentBookingResponse bookAppointment(AppointmentBookingRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        var user = usersRepository.findByUsernameAndActiveTrue(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        var patient = patientRepository.findByUserAccountId(user.getId())
                .filter(item -> item.isActive())
                .orElseThrow(() -> new AppException(ErrorCode.PATIENT_NOT_FOUND));

        var doctor = doctorRepository.findById(request.getDoctorId())
                .filter(item -> item.isActive())
                .orElseThrow(() -> new AppException(ErrorCode.DOCTOR_NOT_FOUND));

        var timeSlot = timeSlotConfigRepository.findById(request.getTimeSlotId())
                .filter(item -> item.isActive())
                .orElseThrow(() -> new AppException(ErrorCode.SLOT_NOT_FOUND));

        if (!timeSlot.isEnabled()) {
            throw new AppException(ErrorCode.SLOT_DISABLED);
        }

        boolean doctorSlotBooked = appointmentRepository.existsByDoctorIdAndAppointmentDateAndTimeSlotConfigIdAndActiveTrue(
                doctor.getId(),
                request.getAppointmentDate(),
                timeSlot.getId()
        );
        if (doctorSlotBooked) {
            throw new AppException(ErrorCode.APPOINTMENT_DUPLICATED);
        }

        boolean duplicatedByPatient = appointmentRepository
                .existsByPatientIdAndDoctorIdAndAppointmentDateAndTimeSlotConfigIdAndActiveTrue(
                        patient.getId(),
                        doctor.getId(),
                        request.getAppointmentDate(),
                        timeSlot.getId()
                );
        if (duplicatedByPatient) {
            throw new AppException(ErrorCode.APPOINTMENT_DUPLICATED);
        }

        var appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .timeSlotConfig(timeSlot)
                .appointmentDate(request.getAppointmentDate())
                .status(AppointmentStatus.PENDING)
                .depositAmount(BigDecimal.ZERO)
                .reason(request.getReason())
                .note(request.getNote())
                .build();

        Appointment savedAppointment = appointmentRepository.save(appointment);

        return AppointmentBookingResponse.builder()
                .appointmentId(savedAppointment.getId())
                .patientId(patient.getId())
                .doctorId(doctor.getId())
                .timeSlotId(timeSlot.getId())
                .appointmentDate(savedAppointment.getAppointmentDate())
                .status(savedAppointment.getStatus())
                .depositAmount(savedAppointment.getDepositAmount())
                .reason(savedAppointment.getReason())
                .note(savedAppointment.getNote())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('PATIENT')")
    public PageResponse<AppointmentHistoryResponse> getMyAppointmentHistory(AppointmentStatus status, int page, int size) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        var user = usersRepository.findByUsernameAndActiveTrue(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        var patient = patientRepository.findByUserAccountId(user.getId())
                .filter(item -> item.isActive())
                .orElseThrow(() -> new AppException(ErrorCode.PATIENT_NOT_FOUND));

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "appointmentDate", "createdAt")
        );
        Page<Appointment> appointments = status == null
                ? appointmentRepository.findByPatientIdAndActiveTrue(patient.getId(), pageable)
                : appointmentRepository.findByPatientIdAndStatusAndActiveTrue(patient.getId(), status, pageable);

        return PageResponse.<AppointmentHistoryResponse>builder()
                .content(appointments.getContent().stream().map(this::toHistoryResponse).toList())
                .page(appointments.getNumber())
                .size(appointments.getSize())
                .totalElements(appointments.getTotalElements())
                .totalPages(appointments.getTotalPages())
                .build();
    }

    private AppointmentHistoryResponse toHistoryResponse(Appointment appointment) {
        return AppointmentHistoryResponse.builder()
                .appointmentId(appointment.getId())
                .doctorId(appointment.getDoctor().getId())
                .doctorName(appointment.getDoctor().getUserAccount().getFullName())
                .timeSlotId(appointment.getTimeSlotConfig().getId())
                .timeSlot(
                        appointment.getTimeSlotConfig().getStartTime()
                                + " - "
                                + appointment.getTimeSlotConfig().getEndTime()
                )
                .appointmentDate(appointment.getAppointmentDate())
                .status(appointment.getStatus())
                .depositAmount(appointment.getDepositAmount())
                .reason(appointment.getReason())
                .note(appointment.getNote())
                .build();
    }
}
