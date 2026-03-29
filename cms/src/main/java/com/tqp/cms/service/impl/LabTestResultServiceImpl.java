package com.tqp.cms.service.impl;

import com.tqp.cms.dto.request.LabTestResultRequest;
import com.tqp.cms.entity.LabTestOrder;
import com.tqp.cms.entity.LabTestOrderStatus;
import com.tqp.cms.entity.LabTestResult;
import com.tqp.cms.exception.AppException;
import com.tqp.cms.exception.ErrorCode;
import com.tqp.cms.mapper.LabTestResultMapper;
import com.tqp.cms.repository.LabTestOrderRepository;
import com.tqp.cms.repository.LabTestResultRepository;
import com.tqp.cms.service.LabTestResultService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LabTestResultServiceImpl implements LabTestResultService {
     LabTestOrderRepository labTestOrderRepository;
     LabTestResultRepository labTestResultRepository;
     LabTestResultMapper labTestResultMapper;

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
}
