package com.tqp.cms.repository;

import com.tqp.cms.entity.Specialty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpecialtyRepository extends JpaRepository<Specialty, UUID> {
    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, UUID id);

    Optional<Specialty> findByName(String name);

    Page<Specialty> findByActiveTrue(Pageable pageable);

    Page<Specialty> findByActiveTrueAndNameContainingIgnoreCase(String name, Pageable pageable);
}
