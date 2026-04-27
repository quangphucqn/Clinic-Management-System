package com.tqp.cms.service;

import com.tqp.cms.dto.response.PatientMonthlyStatisticResponse;
import com.tqp.cms.dto.response.PatientQuarterlyStatisticResponse;
import com.tqp.cms.dto.response.PatientYearlyStatisticResponse;
import java.util.List;

public interface PatientStatisticsService {
    List<PatientMonthlyStatisticResponse> getPatientStatisticsByMonth();

    List<PatientQuarterlyStatisticResponse> getPatientStatisticsByQuarter();

    List<PatientYearlyStatisticResponse> getPatientStatisticsByYear();
}
