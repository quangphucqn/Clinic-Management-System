package com.tqp.cms.mapper;

import com.tqp.cms.dto.request.MedicalRecordRequest;
import com.tqp.cms.dto.response.MedicalRecordResponse;
import com.tqp.cms.entity.Appointment;
import com.tqp.cms.entity.Doctor;
import com.tqp.cms.entity.MedicalRecord;
import com.tqp.cms.entity.Patient;
import org.springframework.stereotype.Component;

@Component
public class MedicalRecordMapper {

    public MedicalRecord toEntity(MedicalRecordRequest req,
                                  Appointment appointment,
                                  Patient patient,
                                  Doctor doctor) {

        return MedicalRecord.builder()
                .appointment(appointment)
                .patient(patient)
                .doctor(doctor)
                .symptoms(req.getSymptoms())
                .diagnosis(req.getDiagnosis())
                .conclusion(req.getConclusion())
                .build();
    }

    public MedicalRecordResponse toResponse(MedicalRecord record) {
        return MedicalRecordResponse.builder()
                .id(record.getId())
                .patientName(record.getPatient().getUserAccount().getFullName())
                .diagnosis(record.getDiagnosis())
                .visitedAt(record.getVisitedAt())
                .build();
    }
}
