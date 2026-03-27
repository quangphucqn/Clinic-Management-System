package com.tqp.cms.repository;

import com.tqp.cms.entity.Appointment;
import com.tqp.cms.entity.AppointmentStatus;
import org.hibernate.query.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.awt.print.Pageable;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    List<Appointment> findByDoctorId(UUID doctorId);

    List<Appointment> findByDoctorIdAndAppointmentDate(UUID doctorId, LocalDate appointmentDate);

    List<Appointment> findByDoctorIdAndStatus(UUID doctorId, AppointmentStatus status);

    List<Appointment> findByDoctorIdAndAppointmentDateAndStatus(
            UUID doctorId,
            LocalDate appointmentDate,
            AppointmentStatus status
    );

}
