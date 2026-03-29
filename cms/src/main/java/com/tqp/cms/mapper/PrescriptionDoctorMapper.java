package com.tqp.cms.mapper;

import com.tqp.cms.dto.request.PrescriptionItemDoctorRequest;
import com.tqp.cms.dto.response.PrescriptionDoctorResponse;
import com.tqp.cms.dto.response.PrescriptionItemDoctorResponse;
import com.tqp.cms.entity.*;
import org.springframework.stereotype.Component;

@Component
public class PrescriptionDoctorMapper {

    public Prescription toEntity(MedicalRecord record,
                                 Patient patient,
                                 Doctor doctor,
                                 String instructions) {
        return Prescription.builder()
                .medicalRecord(record)
                .patient(patient)
                .doctor(doctor)
                .instructions(instructions)
                .build();
    }

    public PrescriptionItem toItem(Prescription prescription,
                                   Medicine medicine,
                                   PrescriptionItemDoctorRequest req) {
        return PrescriptionItem.builder()
                .prescription(prescription)
                .medicine(medicine)
                .quantity(req.getQuantity())
                .dosage(req.getDosage())
                .frequency(req.getFrequency())
                .durationDays(req.getDurationDays())
                .note(req.getNote())
                .build();
    }

    public PrescriptionDoctorResponse toResponse(Prescription p) {
        return PrescriptionDoctorResponse.builder()
                .id(p.getId())
                .patientName(p.getPatient().getUserAccount().getFullName())
                .items(p.getItems().stream().map(i ->
                        PrescriptionItemDoctorResponse.builder()
                                .medicineName(i.getMedicine().getName())
                                .quantity(i.getQuantity())
                                .dosage(i.getDosage())
                                .build()
                ).toList())
                .build();
    }
}
