package com.tqp.cms.controller;

import com.tqp.cms.dto.response.ApiResponse;
import com.tqp.cms.dto.response.RevenueMonthlyStatisticResponse;
import com.tqp.cms.dto.response.RevenueQuarterlyStatisticResponse;
import com.tqp.cms.dto.response.RevenueYearlyStatisticResponse;
import com.tqp.cms.service.RevenueStatisticsService;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/statistics/revenue")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@PreAuthorize("hasRole('ADMIN')")
public class RevenueStatisticsController {
    RevenueStatisticsService revenueStatisticsService;

    @GetMapping("/monthly")
    public ApiResponse<List<RevenueMonthlyStatisticResponse>> getRevenueStatsByMonth() {
        return ApiResponse.<List<RevenueMonthlyStatisticResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Get revenue statistics by month successfully")
                .result(revenueStatisticsService.getRevenueStatisticsByMonth())
                .build();
    }

    @GetMapping("/quarterly")
    public ApiResponse<List<RevenueQuarterlyStatisticResponse>> getRevenueStatsByQuarter() {
        return ApiResponse.<List<RevenueQuarterlyStatisticResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Get revenue statistics by quarter successfully")
                .result(revenueStatisticsService.getRevenueStatisticsByQuarter())
                .build();
    }

    @GetMapping("/yearly")
    public ApiResponse<List<RevenueYearlyStatisticResponse>> getRevenueStatsByYear() {
        return ApiResponse.<List<RevenueYearlyStatisticResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Get revenue statistics by year successfully")
                .result(revenueStatisticsService.getRevenueStatisticsByYear())
                .build();
    }
}
