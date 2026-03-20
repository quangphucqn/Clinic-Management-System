package com.tqp.cms.repository;

import com.tqp.cms.entity.Unit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UnitRepository extends JpaRepository<Unit, UUID> {
    Optional<Unit> findByName(String name);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, UUID id);

    Page<Unit> findByActiveTrue(Pageable pageable);

    Page<Unit> findByActiveTrueAndNameContainingIgnoreCase(String name, Pageable pageable);
}
