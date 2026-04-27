package com.tqp.cms.repository;

import com.tqp.cms.entity.Medicine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MedicineRepository extends JpaRepository<Medicine, UUID> {
    boolean existsByCode(String code);

    Optional<Medicine> findByCode(String code);

    @EntityGraph(attributePaths = "unit")
    Page<Medicine> findByActiveTrue(Pageable pageable);

    @EntityGraph(attributePaths = "unit")
    Page<Medicine> findByActiveTrueAndNameContainingIgnoreCase(String name, Pageable pageable);

    @EntityGraph(attributePaths = "unit")
    Page<Medicine> findByActiveTrueAndUnit_NameContainingIgnoreCase(String category, Pageable pageable);

    @EntityGraph(attributePaths = "unit")
    @Query("""
            select m from Medicine m
            where m.active = true
              and lower(m.name) like lower(concat('%', :name, '%'))
              and lower(m.unit.name) like lower(concat('%', :category, '%'))
            """)
    Page<Medicine> findByNameAndCategory(
            @Param("name") String name,
            @Param("category") String category,
            Pageable pageable
    );
}
