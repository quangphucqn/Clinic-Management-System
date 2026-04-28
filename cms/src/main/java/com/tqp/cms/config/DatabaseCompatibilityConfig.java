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
        try {
            jdbcTemplate.execute("ALTER TABLE appointments DROP CONSTRAINT IF EXISTS appointments_doctor_id_appointment_date_time_slot_config_id_key");
            jdbcTemplate.execute("""
                    DO $$
                    DECLARE
                        constraint_name text;
                    BEGIN
                        FOR constraint_name IN
                            SELECT c.conname
                            FROM pg_constraint c
                            JOIN pg_class t ON c.conrelid = t.oid
                            JOIN pg_namespace n ON n.oid = t.relnamespace
                            WHERE c.contype = 'u'
                              AND t.relname = 'appointments'
                              AND n.nspname = current_schema()
                              AND EXISTS (
                                  SELECT 1
                                  FROM pg_attribute a
                                  WHERE a.attrelid = t.oid
                                    AND a.attnum = ANY (c.conkey)
                                    AND a.attname = 'doctor_id'
                              )
                              AND EXISTS (
                                  SELECT 1
                                  FROM pg_attribute a
                                  WHERE a.attrelid = t.oid
                                    AND a.attnum = ANY (c.conkey)
                                    AND a.attname = 'appointment_date'
                              )
                              AND EXISTS (
                                  SELECT 1
                                  FROM pg_attribute a
                                  WHERE a.attrelid = t.oid
                                    AND a.attnum = ANY (c.conkey)
                                    AND a.attname = 'time_slot_config_id'
                              )
                        LOOP
                            EXECUTE format('ALTER TABLE appointments DROP CONSTRAINT IF EXISTS %I', constraint_name);
                        END LOOP;
                    END $$;
                    """);
        } catch (Exception ignored) {
            // Ignore when DB does not support this block or constraint does not exist.
        }
    }
}
