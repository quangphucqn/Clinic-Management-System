package com.tqp.cms.repository;

import com.tqp.cms.entity.Medicine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicineRepository extends JpaRepository<Medicine, UUID> {
    boolean existsByCode(String code);

    Optional<Medicine> findByCode(String code);

    @EntityGraph(attributePaths = "unit")
    Page<Medicine> findByActiveTrue(Pageable pageable);

    @EntityGraph(attributePaths = "unit")
    Page<Medicine> findByActiveTrueAndNameContainingIgnoreCase(String name, Pageable pageable);
}
