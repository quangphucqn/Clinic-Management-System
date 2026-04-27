package com.tqp.cms.config;

import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DatabaseCompatibilityConfig {
    JdbcTemplate jdbcTemplate;

    @PostConstruct
    void relaxPaymentMethodCheckConstraint() {
        try {
            jdbcTemplate.execute("ALTER TABLE payment_transactions DROP CONSTRAINT IF EXISTS payment_transactions_payment_method_check");
        } catch (Exception ignored) {
            // Ignore for non-PostgreSQL or fresh schemas without this constraint.
        }
    }
}
