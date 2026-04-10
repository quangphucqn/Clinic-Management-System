package com.tqp.cms.repository;

import com.tqp.cms.entity.DoctorReview;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorReviewRepository extends JpaRepository<DoctorReview, UUID> {
    boolean existsByAppointmentIdAndActiveTrue(UUID appointmentId);
}
