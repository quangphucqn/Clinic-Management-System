package com.tqp.cms.service;

import com.tqp.cms.dto.request.LabTestOrderRequest;
import com.tqp.cms.dto.response.LabTestOrderResponse;

public interface LabTestOrderService {
    LabTestOrderResponse createLabTestOrder(LabTestOrderRequest request);
}
