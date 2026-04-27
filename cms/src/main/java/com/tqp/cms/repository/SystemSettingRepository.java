package com.tqp.cms.repository;

import com.tqp.cms.entity.SystemSetting;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemSettingRepository extends JpaRepository<SystemSetting, UUID> {
    Optional<SystemSetting> findBySettingKeyAndActiveTrue(String settingKey);
}
