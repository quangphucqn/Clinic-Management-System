package com.tqp.cms.repository;

import com.tqp.cms.entity.PrescriptionItem;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrescriptionItemRepository extends JpaRepository<PrescriptionItem, UUID> {
    List<PrescriptionItem> findByPrescriptionIdAndActiveTrue(UUID prescriptionId);
}
