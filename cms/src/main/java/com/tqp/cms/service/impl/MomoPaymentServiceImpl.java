package com.tqp.cms.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tqp.cms.config.MomoProperties;
import com.tqp.cms.dto.request.MomoIpnRequest;
import com.tqp.cms.dto.response.MomoCreatePaymentResponse;
import com.tqp.cms.service.MomoPaymentService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MomoPaymentServiceImpl implements MomoPaymentService {
    MomoProperties momoProperties;
    ObjectMapper objectMapper;

    @Override
    public MomoCreatePaymentResponse createPayment(String orderId, BigDecimal amount, String orderInfo) {
        if (!momoProperties.isEnabled()) {
            return MomoCreatePaymentResponse.builder()
                    .resultCode(99)
                    .message("MoMo is disabled")
                    .build();
        }

        try {
            String requestId = orderId;
            String amountValue = normalizeAmountForMomo(amount);
            if (amountValue == null) {
                return MomoCreatePaymentResponse.builder()
                        .resultCode(96)
                        .message("Invalid payment amount for MoMo")
                        .build();
            }
            String extraData = "";

            String rawSignature = "accessKey=%s&amount=%s&extraData=%s&ipnUrl=%s&orderId=%s&orderInfo=%s&partnerCode=%s&redirectUrl=%s&requestId=%s&requestType=%s"
                    .formatted(
                            momoProperties.getAccessKey(),
                            amountValue,
                            extraData,
                            momoProperties.getIpnUrl(),
                            orderId,
                            orderInfo,
                            momoProperties.getPartnerCode(),
                            momoProperties.getRedirectUrl(),
                            requestId,
                            momoProperties.getRequestType()
                    );
            String signature = sign(rawSignature, momoProperties.getSecretKey());

            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("partnerCode", momoProperties.getPartnerCode());
            payload.put("accessKey", momoProperties.getAccessKey());
            payload.put("requestId", requestId);
            payload.put("amount", amountValue);
            payload.put("orderId", orderId);
            payload.put("orderInfo", orderInfo);
            payload.put("redirectUrl", momoProperties.getRedirectUrl());
            payload.put("ipnUrl", momoProperties.getIpnUrl());
            payload.put("extraData", extraData);
            payload.put("requestType", momoProperties.getRequestType());
            payload.put("lang", momoProperties.getLang());
            payload.put("signature", signature);

            HttpRequest request = HttpRequest.newBuilder(URI.create(momoProperties.getEndpoint()))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(payload)))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() / 100 != 2) {
                return MomoCreatePaymentResponse.builder()
                        .resultCode(98)
                        .message("MoMo HTTP error: " + response.statusCode())
                        .build();
            }

            Map<String, Object> map = objectMapper.readValue(response.body(), new TypeReference<>() {
            });
            return MomoCreatePaymentResponse.builder()
                    .resultCode((Integer) map.get("resultCode"))
                    .message((String) map.get("message"))
                    .payUrl((String) map.get("payUrl"))
                    .deeplink((String) map.get("deeplink"))
                    .qrCodeUrl((String) map.get("qrCodeUrl"))
                    .requestId((String) map.get("requestId"))
                    .orderId((String) map.get("orderId"))
                    .build();
        } catch (Exception exception) {
            return MomoCreatePaymentResponse.builder()
                    .resultCode(97)
                    .message("MoMo request failed")
                    .build();
        }
    }

    @Override
    public boolean verifyIpnSignature(MomoIpnRequest request) {
        if (request == null || request.getSignature() == null) {
            return false;
        }
        try {
            String rawSignature = "accessKey=%s&amount=%s&extraData=%s&message=%s&orderId=%s&orderInfo=%s&orderType=%s&partnerCode=%s&payType=%s&requestId=%s&responseTime=%s&resultCode=%s&transId=%s"
                    .formatted(
                            momoProperties.getAccessKey(),
                            String.valueOf(request.getAmount()),
                            defaultString(request.getExtraData()),
                            defaultString(request.getMessage()),
                            defaultString(request.getOrderId()),
                            defaultString(request.getOrderInfo()),
                            defaultString(request.getOrderType()),
                            defaultString(request.getPartnerCode()),
                            defaultString(request.getPayType()),
                            defaultString(request.getRequestId()),
                            String.valueOf(request.getResponseTime()),
                            String.valueOf(request.getResultCode()),
                            String.valueOf(request.getTransId())
                    );
            String expected = sign(rawSignature, momoProperties.getSecretKey());
            return expected.equals(request.getSignature());
        } catch (Exception exception) {
            return false;
        }
    }

    private String sign(String data, String secretKey) throws Exception {
        Mac hmacSha256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        hmacSha256.init(keySpec);
        byte[] bytes = hmacSha256.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder builder = new StringBuilder();
        for (byte current : bytes) {
            builder.append(String.format("%02x", current));
        }
        return builder.toString();
    }

    private String defaultString(String value) {
        return value == null ? "" : value;
    }

    private String normalizeAmountForMomo(BigDecimal amount) {
        if (amount == null) {
            return null;
        }
        BigDecimal normalized = amount.setScale(0, RoundingMode.HALF_UP);
        if (normalized.compareTo(BigDecimal.valueOf(1000)) < 0) {
            return null;
        }
        return normalized.toPlainString();
    }
}
