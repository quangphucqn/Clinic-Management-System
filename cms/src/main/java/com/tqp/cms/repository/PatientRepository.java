package com.tqp.cms.repository;

import com.tqp.cms.entity.Patient;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, UUID> {
}
