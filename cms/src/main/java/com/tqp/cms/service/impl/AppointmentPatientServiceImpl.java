package com.tqp.cms.service.impl;

import com.tqp.cms.dto.request.AppointmentBookingRequest;
import com.tqp.cms.dto.request.MomoIpnRequest;
import com.tqp.cms.dto.response.AppointmentBookingResponse;
import com.tqp.cms.dto.response.MomoCreatePaymentResponse;
import com.tqp.cms.dto.response.AppointmentHistoryResponse;
import com.tqp.cms.dto.response.MedicalHistoryResponse;
import com.tqp.cms.dto.response.PageResponse;
import com.tqp.cms.entity.Appointment;
import com.tqp.cms.entity.AppointmentStatus;
import com.tqp.cms.entity.PaymentMethod;
import com.tqp.cms.entity.PaymentStatus;
import com.tqp.cms.exception.AppException;
import com.tqp.cms.exception.ErrorCode;
import com.tqp.cms.repository.AppointmentRepository;
import com.tqp.cms.repository.DoctorRepository;
import com.tqp.cms.repository.DoctorReviewRepository;
import com.tqp.cms.repository.MedicalRecordRepository;
import com.tqp.cms.repository.PatientRepository;
import com.tqp.cms.repository.PaymentTransactionRepository;
import com.tqp.cms.repository.TimeSlotConfigRepository;
import com.tqp.cms.repository.UsersRepository;
import com.tqp.cms.mapper.MedicalHistoryMapper;
import com.tqp.cms.service.AppointmentDepositConfigService;
import com.tqp.cms.service.AppointmentPatientService;
import com.tqp.cms.service.MomoPaymentService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AppointmentPatientServiceImpl implements AppointmentPatientService {
    AppointmentRepository appointmentRepository;
    UsersRepository usersRepository;
    PatientRepository patientRepository;
    DoctorRepository doctorRepository;
    TimeSlotConfigRepository timeSlotConfigRepository;
    PaymentTransactionRepository paymentTransactionRepository;
    MomoPaymentService momoPaymentService;
    AppointmentDepositConfigService appointmentDepositConfigService;
    MedicalRecordRepository medicalRecordRepository;
    MedicalHistoryMapper medicalHistoryMapper;
    DoctorReviewRepository doctorReviewRepository;

    @Override
    @Transactional
    @PreAuthorize("hasRole('PATIENT')")
    public AppointmentBookingResponse bookAppointment(AppointmentBookingRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        var user = usersRepository.findByUsernameAndActiveTrue(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        var patient = patientRepository.findByUserAccountId(user.getId())
                .filter(item -> item.isActive())
                .orElseThrow(() -> new AppException(ErrorCode.PATIENT_NOT_FOUND));

        var doctor = doctorRepository.findById(request.getDoctorId())
                .filter(item -> item.isActive())
                .orElseThrow(() -> new AppException(ErrorCode.DOCTOR_NOT_FOUND));

        var timeSlot = timeSlotConfigRepository.findById(request.getTimeSlotId())
                .filter(item -> item.isActive())
                .orElseThrow(() -> new AppException(ErrorCode.SLOT_NOT_FOUND));

        if (request.getPaymentMethod() != PaymentMethod.MOMO) {
            throw new AppException(ErrorCode.PAYMENT_METHOD_NOT_SUPPORTED);
        }

        if (!timeSlot.isEnabled()) {
            throw new AppException(ErrorCode.SLOT_DISABLED);
        }

        boolean doctorSlotBooked = appointmentRepository.existsByDoctorIdAndAppointmentDateAndTimeSlotConfigIdAndActiveTrue(
                doctor.getId(),
                request.getAppointmentDate(),
                timeSlot.getId()
        );
        if (doctorSlotBooked) {
            throw new AppException(ErrorCode.APPOINTMENT_DUPLICATED);
        }

        boolean duplicatedByPatient = appointmentRepository
                .existsByPatientIdAndDoctorIdAndAppointmentDateAndTimeSlotConfigIdAndActiveTrue(
                        patient.getId(),
                        doctor.getId(),
                        request.getAppointmentDate(),
                        timeSlot.getId()
                );
        if (duplicatedByPatient) {
            throw new AppException(ErrorCode.APPOINTMENT_DUPLICATED);
        }

        String transactionCode = resolveTransactionCode(request.getTransactionCode());
        PaymentStatus paymentStatus = PaymentStatus.PENDING;
        String paymentUrl = null;
        BigDecimal depositAmount = appointmentDepositConfigService.getCurrentDepositAmount();

        var appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .timeSlotConfig(timeSlot)
                .appointmentDate(request.getAppointmentDate())
                .status(AppointmentStatus.PENDING)
                .depositAmount(BigDecimal.ZERO)
                .reason(request.getReason())
                .note(request.getNote())
                .build();

        Appointment savedAppointment = appointmentRepository.save(appointment);

        if (paymentTransactionRepository.existsByTransactionCodeAndActiveTrue(transactionCode)) {
            paymentStatus = PaymentStatus.FAILED;
            return toBookingResponse(savedAppointment, patient.getId(), doctor.getId(), timeSlot.getId(), null,
                    request.getPaymentMethod(), paymentStatus, transactionCode, null);
        }

        var paymentTransaction = paymentTransactionRepository.save(
                com.tqp.cms.entity.PaymentTransaction.builder()
                        .appointment(savedAppointment)
                        .patient(patient)
                        .amount(depositAmount)
                        .paymentMethod(request.getPaymentMethod())
                        .paymentStatus(PaymentStatus.PENDING)
                        .transactionCode(transactionCode)
                        .build()
        );

        MomoCreatePaymentResponse momoResponse = momoPaymentService.createPayment(
                transactionCode,
                depositAmount,
                "Thanh toan lich kham " + savedAppointment.getId()
        );
        if (momoResponse.getResultCode() != null && momoResponse.getResultCode() == 0 && momoResponse.getPayUrl() != null) {
            paymentStatus = PaymentStatus.PENDING;
            paymentUrl = momoResponse.getPayUrl();
        } else {
            paymentStatus = PaymentStatus.FAILED;
            paymentTransaction.setPaymentStatus(PaymentStatus.FAILED);
            paymentTransactionRepository.save(paymentTransaction);
        }

        return toBookingResponse(
                savedAppointment,
                patient.getId(),
                doctor.getId(),
                timeSlot.getId(),
                paymentTransaction.getId(),
                request.getPaymentMethod(),
                paymentStatus,
                transactionCode,
                paymentUrl
        );
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('PATIENT')")
    public PageResponse<AppointmentHistoryResponse> getMyAppointmentHistory(AppointmentStatus status, int page, int size) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        var user = usersRepository.findByUsernameAndActiveTrue(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        var patient = patientRepository.findByUserAccountId(user.getId())
                .filter(item -> item.isActive())
                .orElseThrow(() -> new AppException(ErrorCode.PATIENT_NOT_FOUND));

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "appointmentDate", "createdAt")
        );
        Page<Appointment> appointments = status == null
                ? appointmentRepository.findByPatientIdAndActiveTrue(patient.getId(), pageable)
                : appointmentRepository.findByPatientIdAndStatusAndActiveTrue(patient.getId(), status, pageable);

        return PageResponse.<AppointmentHistoryResponse>builder()
                .content(appointments.getContent().stream().map(item -> toHistoryResponse(item, patient.getId())).toList())
                .page(appointments.getNumber())
                .size(appointments.getSize())
                .totalElements(appointments.getTotalElements())
                .totalPages(appointments.getTotalPages())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('PATIENT')")
    public PageResponse<MedicalHistoryResponse> getMyMedicalHistory(int page, int size) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        var user = usersRepository.findByUsernameAndActiveTrue(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        var patient = patientRepository.findByUserAccountId(user.getId())
                .filter(item -> item.isActive())
                .orElseThrow(() -> new AppException(ErrorCode.PATIENT_NOT_FOUND));

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "visitedAt")
        );

        var medicalRecords = medicalRecordRepository.findByPatientId(patient.getId(), pageable);

        return PageResponse.<MedicalHistoryResponse>builder()
                .content(medicalRecords.getContent().stream().map(medicalHistoryMapper::toSummary).toList())
                .page(medicalRecords.getNumber())
                .size(medicalRecords.getSize())
                .totalElements(medicalRecords.getTotalElements())
                .totalPages(medicalRecords.getTotalPages())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('PATIENT')")
    public List<UUID> getBookedTimeSlotIds(UUID doctorId, LocalDate appointmentDate) {
        doctorRepository.findById(doctorId)
                .filter(item -> item.isActive())
                .orElseThrow(() -> new AppException(ErrorCode.DOCTOR_NOT_FOUND));

        return appointmentRepository.findByDoctorIdAndAppointmentDateAndActiveTrue(doctorId, appointmentDate)
                .stream()
                .map(appointment -> appointment.getTimeSlotConfig().getId())
                .distinct()
                .toList();
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('PATIENT')")
    public AppointmentBookingResponse retryAppointmentPayment(UUID appointmentId, PaymentMethod paymentMethod) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        var user = usersRepository.findByUsernameAndActiveTrue(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        var patient = patientRepository.findByUserAccountId(user.getId())
                .filter(item -> item.isActive())
                .orElseThrow(() -> new AppException(ErrorCode.PATIENT_NOT_FOUND));

        var appointment = appointmentRepository.findById(appointmentId)
                .filter(item -> item.isActive())
                .orElseThrow(() -> new AppException(ErrorCode.APPOINTMENT_NOT_FOUND));

        if (!appointment.getPatient().getId().equals(patient.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        if (appointment.getAppointmentDate().isBefore(LocalDate.now())) {
            throw new AppException(ErrorCode.PAYMENT_EXPIRED);
        }

        var latestTransaction = paymentTransactionRepository
                .findTopByAppointment_IdAndActiveTrueOrderByPaidAtDesc(appointmentId)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));

        if (latestTransaction.getPaymentStatus() == PaymentStatus.SUCCESS
                || appointment.getStatus() == AppointmentStatus.CONFIRMED) {
            return toBookingResponse(
                    appointment,
                    patient.getId(),
                    appointment.getDoctor().getId(),
                    appointment.getTimeSlotConfig().getId(),
                    latestTransaction.getId(),
                    latestTransaction.getPaymentMethod(),
                    PaymentStatus.SUCCESS,
                    latestTransaction.getTransactionCode(),
                    null
            );
        }

        if (paymentMethod != null && paymentMethod != PaymentMethod.MOMO) {
            throw new AppException(ErrorCode.PAYMENT_METHOD_NOT_SUPPORTED);
        }
        PaymentMethod resolvedPaymentMethod = PaymentMethod.MOMO;
        String transactionCode = resolveTransactionCode(null);
        while (paymentTransactionRepository.existsByTransactionCodeAndActiveTrue(transactionCode)) {
            transactionCode = resolveTransactionCode(null);
        }

        var retryTransaction = paymentTransactionRepository.save(
                com.tqp.cms.entity.PaymentTransaction.builder()
                        .appointment(appointment)
                        .patient(patient)
                        .amount(latestTransaction.getAmount())
                        .paymentMethod(resolvedPaymentMethod)
                        .paymentStatus(PaymentStatus.PENDING)
                        .transactionCode(transactionCode)
                        .build()
        );

        PaymentStatus paymentStatus;
        String paymentUrl = null;

        MomoCreatePaymentResponse momoResponse = momoPaymentService.createPayment(
                transactionCode,
                retryTransaction.getAmount(),
                "Thanh toan lich kham " + appointment.getId()
        );
        if (momoResponse.getResultCode() != null && momoResponse.getResultCode() == 0 && momoResponse.getPayUrl() != null) {
            paymentStatus = PaymentStatus.PENDING;
            paymentUrl = momoResponse.getPayUrl();
        } else {
            paymentStatus = PaymentStatus.FAILED;
            retryTransaction.setPaymentStatus(PaymentStatus.FAILED);
            paymentTransactionRepository.save(retryTransaction);
        }

        return toBookingResponse(
                appointment,
                patient.getId(),
                appointment.getDoctor().getId(),
                appointment.getTimeSlotConfig().getId(),
                retryTransaction.getId(),
                resolvedPaymentMethod,
                paymentStatus,
                transactionCode,
                paymentUrl
        );
    }

    @Override
    @Transactional
    public Map<String, Object> handleMomoIpn(MomoIpnRequest request) {
        Map<String, Object> response = new HashMap<>();
        if (!momoPaymentService.verifyIpnSignature(request)) {
            response.put("resultCode", 40);
            response.put("message", "Invalid signature");
            return response;
        }

        var paymentTransaction = paymentTransactionRepository.findByTransactionCodeAndActiveTrue(request.getOrderId())
                .orElse(null);
        if (paymentTransaction == null) {
            response.put("resultCode", 0);
            response.put("message", "Confirm Success");
            return response;
        }

        var appointment = paymentTransaction.getAppointment();
        if (request.getResultCode() != null && request.getResultCode() == 0) {
            paymentTransaction.setPaymentStatus(PaymentStatus.SUCCESS);
            if (appointment != null) {
                appointment.setStatus(AppointmentStatus.CONFIRMED);
                appointment.setDepositAmount(paymentTransaction.getAmount());
                appointmentRepository.save(appointment);
            }
        } else {
            paymentTransaction.setPaymentStatus(PaymentStatus.FAILED);
        }
        paymentTransactionRepository.save(paymentTransaction);

        response.put("resultCode", 0);
        response.put("message", "Confirm Success");
        return response;
    }

    private AppointmentBookingResponse toBookingResponse(
            Appointment appointment,
            UUID patientId,
            UUID doctorId,
            UUID timeSlotId,
            UUID paymentTransactionId,
            PaymentMethod paymentMethod,
            PaymentStatus paymentStatus,
            String transactionCode,
            String paymentUrl
    ) {
        return AppointmentBookingResponse.builder()
                .appointmentId(appointment.getId())
                .patientId(patientId)
                .doctorId(doctorId)
                .timeSlotId(timeSlotId)
                .appointmentDate(appointment.getAppointmentDate())
                .status(appointment.getStatus())
                .depositAmount(appointment.getDepositAmount())
                .reason(appointment.getReason())
                .note(appointment.getNote())
                .paymentTransactionId(paymentTransactionId)
                .paymentMethod(paymentMethod)
                .paymentStatus(paymentStatus)
                .transactionCode(transactionCode)
                .paymentUrl(paymentUrl)
                .build();
    }

    private String resolveTransactionCode(String transactionCode) {
        if (transactionCode == null || transactionCode.isBlank()) {
            return "APPT" + UUID.randomUUID().toString().replace("-", "").substring(0, 20);
        }
        return transactionCode.trim();
    }

    private AppointmentHistoryResponse toHistoryResponse(Appointment appointment, UUID patientId) {
        var latestTransaction = paymentTransactionRepository
                .findTopByAppointment_IdAndActiveTrueOrderByPaidAtDesc(appointment.getId())
                .orElse(null);
        boolean reviewed = doctorReviewRepository.existsByAppointmentIdAndPatientIdAndActiveTrue(
                appointment.getId(),
                patientId
        );
        return AppointmentHistoryResponse.builder()
                .appointmentId(appointment.getId())
                .doctorId(appointment.getDoctor().getId())
                .doctorName(appointment.getDoctor().getUserAccount().getFullName())
                .timeSlotId(appointment.getTimeSlotConfig().getId())
                .timeSlot(
                        appointment.getTimeSlotConfig().getStartTime()
                                + " - "
                                + appointment.getTimeSlotConfig().getEndTime()
                )
                .appointmentDate(appointment.getAppointmentDate())
                .status(appointment.getStatus())
                .depositAmount(appointment.getDepositAmount())
                .paymentMethod(latestTransaction != null ? latestTransaction.getPaymentMethod() : null)
                .paymentStatus(latestTransaction != null ? latestTransaction.getPaymentStatus() : null)
                .transactionCode(latestTransaction != null ? latestTransaction.getTransactionCode() : null)
                .reviewed(reviewed)
                .reason(appointment.getReason())
                .note(appointment.getNote())
                .build();
    }
}
