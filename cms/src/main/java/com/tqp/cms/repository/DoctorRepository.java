package com.tqp.cms.repository;

import com.tqp.cms.entity.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface DoctorRepository extends JpaRepository<Doctor, UUID> {
    boolean existsByLicenseNumber(String licenseNumber);

    boolean existsByLicenseNumberAndIdNot(String licenseNumber, UUID id);

    boolean existsByUserAccountId(UUID userId);

    @EntityGraph(attributePaths = {"userAccount", "specialty"})
    Optional<Doctor> findByUserAccountId(UUID userId);

    @EntityGraph(attributePaths = {"userAccount", "specialty"})
    Page<Doctor> findByActiveTrue(Pageable pageable);

    @EntityGraph(attributePaths = {"userAccount", "specialty"})
    @Query("""
            select d from Doctor d
            where d.active = true
              and (
                   lower(d.licenseNumber) like lower(concat('%', :keyword, '%'))
                   or lower(d.userAccount.fullName) like lower(concat('%', :keyword, '%'))
              )
            """)
    Page<Doctor> searchActiveDoctors(@Param("keyword") String keyword, Pageable pageable);

    @Query("""
            select d.userAccount.id
            from Doctor d
            where d.id = :doctorId and d.active = true
            """)
    Optional<UUID> findActiveUserIdByDoctorId(@Param("doctorId") UUID doctorId);

    @Modifying
    @Query("""
            update Doctor d
            set d.active = false
            where d.id = :doctorId and d.active = true
            """)
    int softDeleteById(@Param("doctorId") UUID doctorId);
}
