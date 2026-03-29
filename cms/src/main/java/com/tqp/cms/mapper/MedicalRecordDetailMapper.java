package com.tqp.cms.mapper;

import com.tqp.cms.dto.response.MedicalRecordDetailResponse;
import com.tqp.cms.entity.LabTestOrder;
import com.tqp.cms.entity.MedicalRecord;
import org.springframework.stereotype.Component;

@Component
public class MedicalRecordDetailMapper {
    public MedicalRecordDetailResponse toDetailResponse(MedicalRecord record) {
        return MedicalRecordDetailResponse.builder()
                .id(record.getId())
                .patientName(record.getPatient().getUserAccount().getFullName())
                .diagnosis(record.getDiagnosis())
                .symptoms(record.getSymptoms())
                .conclusion(record.getConclusion())
                .visitedAt(record.getVisitedAt())
                .prescriptionId(
                        record.getPrescription() != null ? record.getPrescription().getId() : null
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
