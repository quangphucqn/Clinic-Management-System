package com.tqp.cms.repository;

import com.tqp.cms.entity.LabTestOrder;
import com.tqp.cms.entity.LabTestResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LabTestResultRepository extends JpaRepository<LabTestResult, UUID> {

    Optional<LabTestResult> findByLabTestOrderId(UUID labTestOrderId);
}
