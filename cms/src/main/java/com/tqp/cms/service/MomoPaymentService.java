package com.tqp.cms.service;

import com.tqp.cms.dto.request.MomoIpnRequest;
import com.tqp.cms.dto.response.MomoCreatePaymentResponse;
import java.math.BigDecimal;

public interface MomoPaymentService {
    MomoCreatePaymentResponse createPayment(String orderId, BigDecimal amount, String orderInfo);

    boolean verifyIpnSignature(MomoIpnRequest request);
}
