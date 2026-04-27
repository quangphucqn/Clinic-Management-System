package com.tqp.cms.service;

import com.tqp.cms.dto.request.AppointmentBookingRequest;
import com.tqp.cms.dto.request.MomoIpnRequest;
import com.tqp.cms.dto.response.AppointmentBookingResponse;
import com.tqp.cms.dto.response.AppointmentHistoryResponse;
import com.tqp.cms.dto.response.MedicalHistoryResponse;
import com.tqp.cms.dto.response.PageResponse;
import com.tqp.cms.entity.AppointmentStatus;
import com.tqp.cms.entity.PaymentMethod;
import java.time.LocalDate;
import java.util.Map;
import java.util.List;
import java.util.UUID;

public interface AppointmentPatientService {
    AppointmentBookingResponse bookAppointment(AppointmentBookingRequest request);

    PageResponse<AppointmentHistoryResponse> getMyAppointmentHistory(AppointmentStatus status, int page, int size);

    PageResponse<MedicalHistoryResponse> getMyMedicalHistory(int page, int size);

    List<UUID> getBookedTimeSlotIds(UUID doctorId, LocalDate appointmentDate);

    AppointmentBookingResponse retryAppointmentPayment(UUID appointmentId, PaymentMethod paymentMethod);

    Map<String, Object> handleMomoIpn(MomoIpnRequest request);
}
