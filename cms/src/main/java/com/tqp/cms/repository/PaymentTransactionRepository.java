package com.tqp.cms.repository;

import com.tqp.cms.entity.PaymentStatus;
import com.tqp.cms.entity.PaymentTransaction;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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

    @Query(value = """
            select
                extract(year from pt.paid_at)::int as year,
                extract(month from pt.paid_at)::int as month,
                coalesce(sum(pt.amount), 0) as total_amount,
                count(*) as total_transactions
            from payment_transactions pt
            where pt.active = true
              and pt.payment_status = 'SUCCESS'
            group by extract(year from pt.paid_at), extract(month from pt.paid_at)
            order by year desc, month desc
            """, nativeQuery = true)
    List<Object[]> sumRevenueByMonth();

    @Query(value = """
            select
                extract(year from pt.paid_at)::int as year,
                extract(quarter from pt.paid_at)::int as quarter,
                coalesce(sum(pt.amount), 0) as total_amount,
                count(*) as total_transactions
            from payment_transactions pt
            where pt.active = true
              and pt.payment_status = 'SUCCESS'
            group by extract(year from pt.paid_at), extract(quarter from pt.paid_at)
            order by year desc, quarter desc
            """, nativeQuery = true)
    List<Object[]> sumRevenueByQuarter();

    @Query(value = """
            select
                extract(year from pt.paid_at)::int as year,
                coalesce(sum(pt.amount), 0) as total_amount,
                count(*) as total_transactions
            from payment_transactions pt
            where pt.active = true
              and pt.payment_status = 'SUCCESS'
            group by extract(year from pt.paid_at)
            order by year desc
            """, nativeQuery = true)
    List<Object[]> sumRevenueByYear();
}
