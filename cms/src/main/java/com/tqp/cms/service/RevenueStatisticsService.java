package com.tqp.cms.service;

import com.tqp.cms.dto.response.RevenueMonthlyStatisticResponse;
import com.tqp.cms.dto.response.RevenueQuarterlyStatisticResponse;
import com.tqp.cms.dto.response.RevenueYearlyStatisticResponse;
import java.util.List;

public interface RevenueStatisticsService {
    List<RevenueMonthlyStatisticResponse> getRevenueStatisticsByMonth();

    List<RevenueQuarterlyStatisticResponse> getRevenueStatisticsByQuarter();

    List<RevenueYearlyStatisticResponse> getRevenueStatisticsByYear();
}
