package com.tqp.cms.service;

import com.tqp.cms.dto.request.LabTestOrderRequest;
import com.tqp.cms.dto.response.LabTestOrderDetailResponse;
import com.tqp.cms.dto.response.LabTestOrderResponse;

import java.util.UUID;

public interface LabTestOrderService {
    LabTestOrderResponse createLabTestOrder(LabTestOrderRequest request);
    LabTestOrderDetailResponse getLabTestOrderById(UUID LabTestOrderId);
}
