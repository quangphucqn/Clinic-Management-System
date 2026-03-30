package com.tqp.cms.mapper;

import com.tqp.cms.dto.response.MedicalHistoryResponse;
import com.tqp.cms.entity.LabTestOrder;
import com.tqp.cms.entity.MedicalRecord;
import org.springframework.stereotype.Component;

@Component
public class MedicalHistoryMapper {

    public MedicalHistoryResponse toSummary(MedicalRecord record) {

        return MedicalHistoryResponse.builder()
                .medicalRecordId(record.getId())
                .visitedAt(record.getVisitedAt())
                .diagnosis(record.getDiagnosis())
                .doctorName(record.getDoctor().getUserAccount().getFullName())
                .prescriptionId(
                        record.getPrescription() != null
                                ? record.getPrescription().getId()
                                : null
                )
                .labTestOrderIds(
                        record.getLabTestOrders()
                                .stream()
                                .map(LabTestOrder::getId)
                                .toList()
                )
                .build();
    }
}
