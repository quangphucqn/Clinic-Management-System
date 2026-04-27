package com.tqp.cms.service;

import com.tqp.cms.dto.request.AppointmentDepositConfigUpdateRequest;
import com.tqp.cms.dto.response.AppointmentDepositConfigResponse;
import java.math.BigDecimal;

public interface AppointmentDepositConfigService {
    AppointmentDepositConfigResponse getConfig();

    AppointmentDepositConfigResponse updateConfig(AppointmentDepositConfigUpdateRequest request);

    BigDecimal getCurrentDepositAmount();
}
