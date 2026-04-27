package com.tqp.cms.repository;

import com.tqp.cms.entity.Patient;
import java.util.UUID;
import java.util.Optional;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;

public interface PatientRepository extends JpaRepository<Patient, UUID> {
    @EntityGraph(attributePaths = "userAccount")
    Optional<Patient> findByUserAccountId(UUID userAccountId);

    Page<Patient> findByUserAccount_FullNameContainingIgnoreCase(
            String fullName,
            Pageable pageable
    );

    @Query(value = """
            select
                extract(year from p.created_at)::int as year,
                extract(month from p.created_at)::int as month,
                count(*) as total
            from patients p
            group by extract(year from p.created_at), extract(month from p.created_at)
            order by year desc, month desc
            """, nativeQuery = true)
    List<Object[]> countPatientsByMonth();

    @Query(value = """
            select
                extract(year from p.created_at)::int as year,
                extract(quarter from p.created_at)::int as quarter,
                count(*) as total
            from patients p
            group by extract(year from p.created_at), extract(quarter from p.created_at)
            order by year desc, quarter desc
            """, nativeQuery = true)
    List<Object[]> countPatientsByQuarter();

    @Query(value = """
            select
                extract(year from p.created_at)::int as year,
                count(*) as total
            from patients p
            group by extract(year from p.created_at)
            order by year desc
            """, nativeQuery = true)
    List<Object[]> countPatientsByYear();
}
