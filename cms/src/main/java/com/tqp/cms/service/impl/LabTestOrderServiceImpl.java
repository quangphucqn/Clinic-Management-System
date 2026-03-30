package com.tqp.cms.service.impl;

import com.tqp.cms.dto.request.LabTestOrderRequest;
import com.tqp.cms.dto.response.LabTestOrderDetailResponse;
import com.tqp.cms.dto.response.LabTestOrderResponse;
import com.tqp.cms.entity.LabTestOrder;
import com.tqp.cms.entity.MedicalRecord;
import com.tqp.cms.exception.AppException;
import com.tqp.cms.exception.ErrorCode;
import com.tqp.cms.mapper.LabTestMapper;
import com.tqp.cms.mapper.LabTestOderMapper;
import com.tqp.cms.repository.DoctorRepository;
import com.tqp.cms.repository.LabTestOrderRepository;
import com.tqp.cms.repository.MedicalRecordRepository;
import com.tqp.cms.repository.UsersRepository;
import com.tqp.cms.service.LabTestOrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LabTestOrderServiceImpl implements LabTestOrderService {
    MedicalRecordRepository medicalRecordRepository;
    LabTestOrderRepository labTestOrderRepository;
    LabTestOderMapper labTestOderMapper;
    LabTestMapper labTestMapper;
    UsersRepository usersRepository;
    DoctorRepository doctorRepository;


    @Override
    public LabTestOrderResponse createLabTestOrder(LabTestOrderRequest request) {

        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        var user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        var doctor = doctorRepository.findByUserAccountId(user.getId())
                .filter(item -> item.isActive())
                .orElseThrow(() -> new AppException(ErrorCode.DOCTOR_NOT_FOUND));

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

    @Override
    @Transactional(readOnly = true)
    public LabTestOrderDetailResponse getLabTestOrderById(UUID id) {
        LabTestOrder order = labTestOrderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.LAB_TEST_ORDER_NOT_FOUND));
        return labTestMapper.toDetailResponse(order);
    }

}
