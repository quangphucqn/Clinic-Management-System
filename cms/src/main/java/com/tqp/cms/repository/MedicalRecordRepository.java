package com.tqp.cms.repository;

import com.tqp.cms.entity.MedicalRecord;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, UUID> {

    boolean existsByAppointmentId(UUID appointmentId);

    List<MedicalRecord> findByPatientIdOrderByVisitedAtDesc(UUID patientId);

    @EntityGraph(attributePaths = {
            "doctor.userAccount",
            "prescription",
            "prescription.items",
            "prescription.items.medicine",
            "labTestOrders",
            "labTestOrders.labTestResult"
    })
    List<MedicalRecord> findWithDetailsByPatientIdOrderByVisitedAtDesc(UUID patientId);
}
