package com.tqp.cms.service.impl;

import com.tqp.cms.dto.request.AppointmentDepositConfigUpdateRequest;
import com.tqp.cms.dto.response.AppointmentDepositConfigResponse;
import com.tqp.cms.entity.SystemSetting;
import com.tqp.cms.repository.SystemSettingRepository;
import com.tqp.cms.service.AppointmentDepositConfigService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AppointmentDepositConfigServiceImpl implements AppointmentDepositConfigService {
    static final String DEPOSIT_SETTING_KEY = "APPOINTMENT_DEPOSIT_AMOUNT";
    static final BigDecimal DEFAULT_DEPOSIT_AMOUNT = new BigDecimal("100000.00");
    static final String CURRENCY = "VND";

    SystemSettingRepository systemSettingRepository;

    @Override
    @Transactional
    public AppointmentDepositConfigResponse getConfig() {
        BigDecimal amount = getCurrentDepositAmount();
        return toResponse(amount);
    }

    @Override
    @Transactional
    public AppointmentDepositConfigResponse updateConfig(AppointmentDepositConfigUpdateRequest request) {
        BigDecimal normalizedAmount = normalizeAmount(request.getAmount());
        SystemSetting setting = systemSettingRepository.findBySettingKeyAndActiveTrue(DEPOSIT_SETTING_KEY)
                .orElseGet(() -> SystemSetting.builder()
                        .settingKey(DEPOSIT_SETTING_KEY)
                        .description("Patient appointment deposit amount")
                        .build());
        setting.setSettingValue(normalizedAmount.toPlainString());
        systemSettingRepository.save(setting);
        return toResponse(normalizedAmount);
    }

    @Override
    @Transactional
    public BigDecimal getCurrentDepositAmount() {
        SystemSetting setting = systemSettingRepository.findBySettingKeyAndActiveTrue(DEPOSIT_SETTING_KEY)
                .orElseGet(this::createDefaultSetting);
        try {
            return normalizeAmount(new BigDecimal(setting.getSettingValue()));
        } catch (RuntimeException exception) {
            BigDecimal fallbackAmount = normalizeAmount(DEFAULT_DEPOSIT_AMOUNT);
            setting.setSettingValue(fallbackAmount.toPlainString());
            systemSettingRepository.save(setting);
            return fallbackAmount;
        }
    }

    private SystemSetting createDefaultSetting() {
        BigDecimal defaultAmount = normalizeAmount(DEFAULT_DEPOSIT_AMOUNT);
        SystemSetting setting = SystemSetting.builder()
                .settingKey(DEPOSIT_SETTING_KEY)
                .settingValue(defaultAmount.toPlainString())
                .description("Patient appointment deposit amount")
                .build();
        return systemSettingRepository.save(setting);
    }

    private BigDecimal normalizeAmount(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP);
    }

    private AppointmentDepositConfigResponse toResponse(BigDecimal amount) {
        return AppointmentDepositConfigResponse.builder()
                .amount(amount)
                .currency(CURRENCY)
                .build();
    }
}
