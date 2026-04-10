package com.tqp.cms.service.impl;

import com.tqp.cms.dto.response.PrescriptionItemResponse;
import com.tqp.cms.dto.response.PageResponse;
import com.tqp.cms.dto.response.PrescriptionResponse;
import com.tqp.cms.entity.Prescription;
import com.tqp.cms.entity.PrescriptionItem;
import com.tqp.cms.exception.AppException;
import com.tqp.cms.exception.ErrorCode;
import com.tqp.cms.repository.PrescriptionItemRepository;
import com.tqp.cms.repository.PrescriptionRepository;
import com.tqp.cms.repository.PatientRepository;
import com.tqp.cms.repository.UsersRepository;
import com.tqp.cms.service.PrescriptionService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PrescriptionServiceImpl implements PrescriptionService {
    PrescriptionRepository prescriptionRepository;
    PrescriptionItemRepository prescriptionItemRepository;
    UsersRepository usersRepository;
    PatientRepository patientRepository;

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

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('PATIENT')")
    public PageResponse<PrescriptionResponse> getMyPrescriptions(LocalDate issuedDate, int page, int size) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        var user = usersRepository.findByUsernameAndActiveTrue(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        var patient = patientRepository.findByUserAccountId(user.getId())
                .filter(item -> item.isActive())
                .orElseThrow(() -> new AppException(ErrorCode.PATIENT_NOT_FOUND));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "issuedAt"));
        Page<Prescription> prescriptions;
        if (issuedDate != null) {
            LocalDateTime from = issuedDate.atStartOfDay();
            LocalDateTime to = issuedDate.plusDays(1).atStartOfDay();
            prescriptions = prescriptionRepository.findByPatientIdAndIssuedAtBetweenAndActiveTrue(
                    patient.getId(), from, to, pageable
            );
        } else {
            prescriptions = prescriptionRepository.findByPatientIdAndActiveTrue(patient.getId(), pageable);
        }

        return PageResponse.<PrescriptionResponse>builder()
                .content(prescriptions.getContent().stream().map(this::toPrescriptionResponse).toList())
                .page(prescriptions.getNumber())
                .size(prescriptions.getSize())
                .totalElements(prescriptions.getTotalElements())
                .totalPages(prescriptions.getTotalPages())
                .build();
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
