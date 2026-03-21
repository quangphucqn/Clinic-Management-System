package com.tqp.cms.service.impl;

import com.tqp.cms.dto.response.PrescriptionItemResponse;
import com.tqp.cms.dto.response.PrescriptionResponse;
import com.tqp.cms.entity.Prescription;
import com.tqp.cms.entity.PrescriptionItem;
import com.tqp.cms.exception.AppException;
import com.tqp.cms.exception.ErrorCode;
import com.tqp.cms.repository.PrescriptionItemRepository;
import com.tqp.cms.repository.PrescriptionRepository;
import com.tqp.cms.service.PrescriptionService;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PrescriptionServiceImpl implements PrescriptionService {
    PrescriptionRepository prescriptionRepository;
    PrescriptionItemRepository prescriptionItemRepository;

    @Override
    @Transactional(readOnly = true)
    public PrescriptionResponse getPrescriptionById(UUID prescriptionId) {
        Prescription prescription = prescriptionRepository.findByIdAndActiveTrue(prescriptionId)
                .orElseThrow(() -> new AppException(ErrorCode.PRESCRIPTION_NOT_FOUND));
        return toPrescriptionResponse(prescription);
    }

    @Override
    @Transactional(readOnly = true)
    public PrescriptionResponse getPrescriptionByMedicalRecordId(UUID medicalRecordId) {
        Prescription prescription = prescriptionRepository.findByMedicalRecordIdAndActiveTrue(medicalRecordId)
                .orElseThrow(() -> new AppException(ErrorCode.PRESCRIPTION_NOT_FOUND));
        return toPrescriptionResponse(prescription);
    }

    private PrescriptionResponse toPrescriptionResponse(Prescription prescription) {
        List<PrescriptionItemResponse> itemResponses = prescriptionItemRepository
                .findByPrescriptionIdAndActiveTrue(prescription.getId())
                .stream()
                .filter(item -> item.getMedicine() != null && item.getMedicine().isActive())
                .map(this::toPrescriptionItemResponse)
                .toList();

        return PrescriptionResponse.builder()
                .id(prescription.getId())
                .medicalRecordId(prescription.getMedicalRecord().getId())
                .patientId(prescription.getPatient().getId())
                .patientName(prescription.getPatient().getUserAccount().getFullName())
                .doctorId(prescription.getDoctor().getId())
                .doctorName(prescription.getDoctor().getUserAccount().getFullName())
                .instructions(prescription.getInstructions())
                .issuedAt(prescription.getIssuedAt())
                .createdAt(prescription.getCreatedAt())
                .updatedAt(prescription.getUpdatedAt())
                .items(itemResponses)
                .build();
    }

    private PrescriptionItemResponse toPrescriptionItemResponse(PrescriptionItem item) {
        return PrescriptionItemResponse.builder()
                .id(item.getId())
                .medicineId(item.getMedicine().getId())
                .medicineCode(item.getMedicine().getCode())
                .medicineName(item.getMedicine().getName())
                .unitName(item.getMedicine().getUnit().getName())
                .quantity(item.getQuantity())
                .dosage(item.getDosage())
                .frequency(item.getFrequency())
                .durationDays(item.getDurationDays())
                .note(item.getNote())
                .build();
    }
}
