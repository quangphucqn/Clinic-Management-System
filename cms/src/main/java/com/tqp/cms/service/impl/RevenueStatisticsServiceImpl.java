package com.tqp.cms.service.impl;

import com.tqp.cms.dto.response.RevenueMonthlyStatisticResponse;
import com.tqp.cms.dto.response.RevenueQuarterlyStatisticResponse;
import com.tqp.cms.dto.response.RevenueYearlyStatisticResponse;
import com.tqp.cms.repository.PaymentTransactionRepository;
import com.tqp.cms.service.RevenueStatisticsService;
import java.math.BigDecimal;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RevenueStatisticsServiceImpl implements RevenueStatisticsService {
    PaymentTransactionRepository paymentTransactionRepository;

    @Override
    public List<RevenueMonthlyStatisticResponse> getRevenueStatisticsByMonth() {
        return paymentTransactionRepository.sumRevenueByMonth()
                .stream()
                .map(row -> RevenueMonthlyStatisticResponse.builder()
                        .year(toInt(row[0]))
                        .month(toInt(row[1]))
                        .totalAmount(toBigDecimal(row[2]))
                        .totalTransactions(toLong(row[3]))
                        .build())
                .toList();
    }

    @Override
    public List<RevenueQuarterlyStatisticResponse> getRevenueStatisticsByQuarter() {
        return paymentTransactionRepository.sumRevenueByQuarter()
                .stream()
                .map(row -> RevenueQuarterlyStatisticResponse.builder()
                        .year(toInt(row[0]))
                        .quarter(toInt(row[1]))
                        .totalAmount(toBigDecimal(row[2]))
                        .totalTransactions(toLong(row[3]))
                        .build())
                .toList();
    }

    @Override
    public List<RevenueYearlyStatisticResponse> getRevenueStatisticsByYear() {
        return paymentTransactionRepository.sumRevenueByYear()
                .stream()
                .map(row -> RevenueYearlyStatisticResponse.builder()
                        .year(toInt(row[0]))
                        .totalAmount(toBigDecimal(row[1]))
                        .totalTransactions(toLong(row[2]))
                        .build())
                .toList();
    }

    private Integer toInt(Object value) {
        return value == null ? null : ((Number) value).intValue();
    }

    private Long toLong(Object value) {
        return value == null ? 0L : ((Number) value).longValue();
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal decimalValue) {
            return decimalValue;
        }
        return new BigDecimal(value.toString());
    }
}
