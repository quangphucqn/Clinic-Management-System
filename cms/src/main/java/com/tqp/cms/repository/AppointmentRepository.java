package com.tqp.cms.repository;

import com.tqp.cms.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    List<Appointment> findByAppointmentDate(LocalDate appointmentDate);
}
