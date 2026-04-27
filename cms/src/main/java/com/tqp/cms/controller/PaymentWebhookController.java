package com.tqp.cms.controller;

import com.tqp.cms.dto.request.MomoIpnRequest;
import com.tqp.cms.service.AppointmentPatientService;
import java.util.Map;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments/momo")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentWebhookController {
    AppointmentPatientService appointmentPatientService;

    @PostMapping("/ipn")
    public ResponseEntity<Map<String, Object>> handleMomoIpn(@RequestBody MomoIpnRequest request) {
        return ResponseEntity.ok(appointmentPatientService.handleMomoIpn(request));
    }
}
