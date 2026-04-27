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
                .appointmentDate(record.getAppointment().getAppointmentDate())
                .timeSlot(
                        record.getAppointment().getTimeSlotConfig().getStartTime()
                                + " - "
                                + record.getAppointment().getTimeSlotConfig().getEndTime()
                )
                .visitedAt(record.getVisitedAt())
                .symptoms(record.getSymptoms())
                .diagnosis(record.getDiagnosis())
                .conclusion(record.getConclusion())
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
