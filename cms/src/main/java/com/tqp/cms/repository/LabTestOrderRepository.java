package com.tqp.cms.repository;

import com.tqp.cms.entity.LabTestOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LabTestOrderRepository extends JpaRepository<LabTestOrder, UUID> {

    List<LabTestOrder> findByMedicalRecordId(UUID medicalRecordId);
}
