package com.tqp.cms.service.impl;

import com.tqp.cms.dto.request.LabTestOrderRequest;
import com.tqp.cms.dto.response.LabTestOrderResponse;
import com.tqp.cms.entity.MedicalRecord;
import com.tqp.cms.exception.AppException;
import com.tqp.cms.exception.ErrorCode;
import com.tqp.cms.mapper.LabTestOderMapper;
import com.tqp.cms.repository.LabTestOrderRepository;
import com.tqp.cms.repository.MedicalRecordRepository;
import com.tqp.cms.repository.UsersRepository;
import com.tqp.cms.service.LabTestOrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LabTestOrderServiceImpl implements LabTestOrderService {
    MedicalRecordRepository medicalRecordRepository;
    LabTestOrderRepository labTestOrderRepository;
    LabTestOderMapper labTestOderMapper;
    UsersRepository usersRepository;

    @Override
    public LabTestOrderResponse createLabTestOrder(LabTestOrderRequest request) {

        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        var user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        var doctor = user.getDoctorProfile();

        MedicalRecord record = medicalRecordRepository.findById(request.getMedicalRecordId())
                .orElseThrow(() -> new AppException(ErrorCode.MEDICAL_RECORD_NOT_FOUND));

        if (!record.getDoctor().getId().equals(doctor.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        return labTestOderMapper.toResponse(
                labTestOrderRepository.save(
                        labTestOderMapper.toEntity(request, record, record.getPatient(), doctor)
                )
        );
    }

}
