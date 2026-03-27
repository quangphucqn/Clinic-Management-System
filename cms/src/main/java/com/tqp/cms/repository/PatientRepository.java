package com.tqp.cms.repository;

import com.tqp.cms.entity.Patient;
import java.util.UUID;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

public interface PatientRepository extends JpaRepository<Patient, UUID> {
    @EntityGraph(attributePaths = "userAccount")
    Optional<Patient> findByUserAccountId(UUID userAccountId);
}
