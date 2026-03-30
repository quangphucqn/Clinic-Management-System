package com.tqp.cms.service.impl;

import com.tqp.cms.dto.request.PrescriptionDoctorRequest;
import com.tqp.cms.dto.response.PrescriptionDoctorResponse;
import com.tqp.cms.entity.MedicalRecord;
import com.tqp.cms.entity.Prescription;
import com.tqp.cms.entity.PrescriptionItem;
import com.tqp.cms.exception.AppException;
import com.tqp.cms.exception.ErrorCode;
import com.tqp.cms.mapper.PrescriptionDoctorMapper;
import com.tqp.cms.repository.*;
import com.tqp.cms.service.PrescriptionDoctorService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PrescriptionDoctorServiceImpl implements PrescriptionDoctorService {


    MedicalRecordRepository recordRepo;
    PrescriptionRepository prescriptionRepo;
    MedicineRepository medicineRepo;
    PrescriptionItemRepository itemRepo;
    PrescriptionDoctorMapper mapper;
    UsersRepository usersRepository;

    @Override
    public PrescriptionDoctorResponse createPrescription (PrescriptionDoctorRequest request) {

        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        var user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        var doctor = user.getDoctorProfile();

        MedicalRecord record = recordRepo.findById(request.getMedicalRecordId())
                .orElseThrow(() -> new AppException(ErrorCode.MEDICAL_RECORD_NOT_FOUND));

        if (!record.getDoctor().getId().equals(doctor.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        if (prescriptionRepo.existsByMedicalRecordId(record.getId())) {
            throw new AppException(ErrorCode.PRESCRIPTION_EXISTED);
        }

        Prescription prescription = mapper.toEntity(
                record,
                record.getPatient(),
                doctor,
                request.getInstructions()
        );

        Prescription savedPrescription = prescriptionRepo.save(prescription);

        List<PrescriptionItem> items = request.getItems().stream()
                .map(i -> mapper.toItem(
                        savedPrescription,
                        medicineRepo.findById(i.getMedicineId())
                                .orElseThrow(() -> new AppException(ErrorCode.MEDICINE_NOT_FOUND)),
                        i
                ))
                .toList();

        itemRepo.saveAll(items);
        savedPrescription.setItems(items);

        return mapper.toResponse(savedPrescription);
    }

    @Transactional(readOnly = true)
    public PrescriptionDoctorResponse getPrescriptionById(UUID id) {
        Prescription prescription = prescriptionRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRESCRIPTION_NOT_FOUND));
        return mapper.toResponse(prescription);
    }
}

