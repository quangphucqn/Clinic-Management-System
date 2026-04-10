package com.tqp.cms.service.impl;

import com.tqp.cms.dto.request.LabTestResultRequest;
import com.tqp.cms.dto.response.PatientLabResultResponse;
import com.tqp.cms.entity.LabTestOrder;
import com.tqp.cms.entity.LabTestOrderStatus;
import com.tqp.cms.entity.LabTestResult;
import com.tqp.cms.exception.AppException;
import com.tqp.cms.exception.ErrorCode;
import com.tqp.cms.mapper.LabTestResultMapper;
import com.tqp.cms.repository.LabTestOrderRepository;
import com.tqp.cms.repository.LabTestResultRepository;
import com.tqp.cms.repository.PatientRepository;
import com.tqp.cms.repository.UsersRepository;
import com.tqp.cms.service.LabTestResultService;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LabTestResultServiceImpl implements LabTestResultService {
     LabTestOrderRepository labTestOrderRepository;
     LabTestResultRepository labTestResultRepository;
     LabTestResultMapper labTestResultMapper;
     UsersRepository usersRepository;
     PatientRepository patientRepository;

    @Override
    public LabTestResult createLabTestResult (LabTestResultRequest request) {

        LabTestOrder order = labTestOrderRepository.findById(request.getLabTestOrderId())
                .orElseThrow(() -> new AppException(ErrorCode.LAB_TEST_ORDER_NOT_FOUND));

        if (labTestResultRepository.findByLabTestOrderId(order.getId()).isPresent()) {
            throw new AppException(ErrorCode.LAB_TEST_RESULT_EXISTED);
        }

        LabTestResult result = labTestResultMapper.toEntity(request, order);

        order.setStatus(LabTestOrderStatus.COMPLETED);

        return labTestResultRepository.save(result);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('PATIENT')")
    public List<PatientLabResultResponse> getMyLabResults() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        var user = usersRepository.findByUsernameAndActiveTrue(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        var patient = patientRepository.findByUserAccountId(user.getId())
                .filter(item -> item.isActive())
                .orElseThrow(() -> new AppException(ErrorCode.PATIENT_NOT_FOUND));

        return labTestResultRepository.findByLabTestOrderPatientIdAndActiveTrueOrderByReportedAtDesc(patient.getId())
                .stream()
                .map(this::toPatientLabResultResponse)
                .toList();
    }

    private PatientLabResultResponse toPatientLabResultResponse(LabTestResult labTestResult) {
        return PatientLabResultResponse.builder()
                .testName(labTestResult.getLabTestOrder().getTestName())
                .result(labTestResult.getResultValue())
                .fileUrl(labTestResult.getAttachmentUrl())
                .date(labTestResult.getReportedAt())
                .build();
    }
}
