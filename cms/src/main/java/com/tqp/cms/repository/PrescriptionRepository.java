package com.tqp.cms.repository;

import com.tqp.cms.entity.Prescription;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrescriptionRepository extends JpaRepository<Prescription, UUID> {
    Optional<Prescription> findByIdAndActiveTrue(UUID id);

    Optional<Prescription> findByMedicalRecordIdAndActiveTrue(UUID medicalRecordId);

    boolean existsByMedicalRecordId(UUID medicalRecordId);

    Optional<Prescription> findByMedicalRecordId(UUID medicalRecordId);

    Page<Prescription> findByPatientIdAndActiveTrue(UUID patientId, Pageable pageable);

    Page<Prescription> findByPatientIdAndIssuedAtBetweenAndActiveTrue(
            UUID patientId,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable
    );
}
