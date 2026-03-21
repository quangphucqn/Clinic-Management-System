package com.tqp.cms.repository;

import com.tqp.cms.entity.Prescription;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrescriptionRepository extends JpaRepository<Prescription, UUID> {
    Optional<Prescription> findByIdAndActiveTrue(UUID id);

    Optional<Prescription> findByMedicalRecordIdAndActiveTrue(UUID medicalRecordId);
}
