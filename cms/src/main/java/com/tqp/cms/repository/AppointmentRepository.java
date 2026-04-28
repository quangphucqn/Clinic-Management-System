package com.tqp.cms.repository;

import com.tqp.cms.entity.Appointment;
import com.tqp.cms.entity.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    Page<Appointment> findByDoctorId(UUID doctorId, Pageable pageable);

    Page<Appointment> findByDoctorIdAndAppointmentDate(
            UUID doctorId,
            LocalDate appointmentDate,
            Pageable pageable
    );

    List<Appointment> findByDoctorIdAndAppointmentDateAndActiveTrue(
            UUID doctorId,
            LocalDate appointmentDate
    );

    Page<Appointment> findByDoctorIdAndStatus(
            UUID doctorId,
            AppointmentStatus status,
            Pageable pageable
    );

    Page<Appointment> findByDoctorIdAndAppointmentDateAndStatus(
            UUID doctorId,
            LocalDate appointmentDate,
            AppointmentStatus status,
            Pageable pageable
    );

    Page<Appointment> findByDoctorIdAndPatient_UserAccount_FullNameContainingIgnoreCase(
            UUID doctorId,
            String fullName,
            Pageable pageable
    );

    boolean existsByPatientIdAndDoctorIdAndAppointmentDateAndTimeSlotConfigIdAndActiveTrue(
            UUID patientId,
            UUID doctorId,
            LocalDate appointmentDate,
            UUID timeSlotConfigId
    );

    boolean existsByPatientIdAndDoctorIdAndAppointmentDateAndTimeSlotConfigIdAndStatusInAndActiveTrue(
            UUID patientId,
            UUID doctorId,
            LocalDate appointmentDate,
            UUID timeSlotConfigId,
            Collection<AppointmentStatus> statuses
    );

    long countByDoctorIdAndAppointmentDateAndTimeSlotConfigIdAndStatusInAndActiveTrue(
            UUID doctorId,
            LocalDate appointmentDate,
            UUID timeSlotConfigId,
            Collection<AppointmentStatus> statuses
    );

    List<Appointment> findByDoctorIdAndAppointmentDateAndStatusInAndActiveTrue(
            UUID doctorId,
            LocalDate appointmentDate,
            Collection<AppointmentStatus> statuses
    );

    Page<Appointment> findByPatientIdAndActiveTrue(UUID patientId, Pageable pageable);

    Page<Appointment> findByPatientIdAndStatusAndActiveTrue(
            UUID patientId,
            AppointmentStatus status,
            Pageable pageable
    );

}
