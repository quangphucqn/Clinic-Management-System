package com.tqp.cms.repository;

import com.tqp.cms.entity.DoctorReview;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorReviewRepository extends JpaRepository<DoctorReview, UUID> {
    boolean existsByAppointmentIdAndActiveTrue(UUID appointmentId);

    boolean existsByAppointmentIdAndPatientIdAndActiveTrue(UUID appointmentId, UUID patientId);

    boolean existsByDoctorIdAndPatientIdAndActiveTrue(UUID doctorId, UUID patientId);

    Page<DoctorReview> findByPatientIdAndActiveTrueOrderByReviewedAtDesc(UUID patientId, Pageable pageable);
}
