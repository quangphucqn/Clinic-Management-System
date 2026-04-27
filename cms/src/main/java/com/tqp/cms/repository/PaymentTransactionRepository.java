package com.tqp.cms.repository;

import com.tqp.cms.entity.PaymentStatus;
import com.tqp.cms.entity.PaymentTransaction;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, UUID> {
    boolean existsByTransactionCodeAndActiveTrue(String transactionCode);

    Optional<PaymentTransaction> findByTransactionCodeAndActiveTrue(String transactionCode);

    Optional<PaymentTransaction> findTopByAppointment_IdAndActiveTrueOrderByPaidAtDesc(UUID appointmentId);

    Optional<PaymentTransaction> findTopByAppointment_IdAndPaymentStatusAndActiveTrueOrderByPaidAtDesc(
            UUID appointmentId,
            PaymentStatus paymentStatus
    );

    boolean existsByAppointment_IdAndPaymentStatusAndActiveTrue(UUID appointmentId, PaymentStatus paymentStatus);

    List<PaymentTransaction> findByAppointment_IdAndActiveTrueOrderByPaidAtDesc(UUID appointmentId);
}
