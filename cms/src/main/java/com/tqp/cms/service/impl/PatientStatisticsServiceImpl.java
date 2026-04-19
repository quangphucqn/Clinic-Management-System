package com.tqp.cms.service.impl;

import com.tqp.cms.dto.response.PatientMonthlyStatisticResponse;
import com.tqp.cms.dto.response.PatientQuarterlyStatisticResponse;
import com.tqp.cms.dto.response.PatientYearlyStatisticResponse;
import com.tqp.cms.repository.PatientRepository;
import com.tqp.cms.service.PatientStatisticsService;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PatientStatisticsServiceImpl implements PatientStatisticsService {
    PatientRepository patientRepository;

    @Override
    public List<PatientMonthlyStatisticResponse> getPatientStatisticsByMonth() {
        return patientRepository.countPatientsByMonth()
                .stream()
                .map(row -> PatientMonthlyStatisticResponse.builder()
                        .year(toInt(row[0]))
                        .month(toInt(row[1]))
                        .totalPatients(toLong(row[2]))
                        .build())
                .toList();
    }

    @Override
    public List<PatientQuarterlyStatisticResponse> getPatientStatisticsByQuarter() {
        return patientRepository.countPatientsByQuarter()
                .stream()
                .map(row -> PatientQuarterlyStatisticResponse.builder()
                        .year(toInt(row[0]))
                        .quarter(toInt(row[1]))
                        .totalPatients(toLong(row[2]))
                        .build())
                .toList();
    }

    @Override
    public List<PatientYearlyStatisticResponse> getPatientStatisticsByYear() {
        return patientRepository.countPatientsByYear()
                .stream()
                .map(row -> PatientYearlyStatisticResponse.builder()
                        .year(toInt(row[0]))
                        .totalPatients(toLong(row[1]))
                        .build())
                .toList();
    }

    private Integer toInt(Object value) {
        return value == null ? null : ((Number) value).intValue();
    }

    private Long toLong(Object value) {
        return value == null ? 0L : ((Number) value).longValue();
    }
}
