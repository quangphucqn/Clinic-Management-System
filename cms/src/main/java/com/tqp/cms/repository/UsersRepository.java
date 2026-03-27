package com.tqp.cms.repository;

import com.tqp.cms.entity.Users;
import com.tqp.cms.entity.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, UUID> {
    Optional<Users> findByUsername(String username);
    Optional<Users> findByUsernameAndActiveTrue(String username);
    Optional<Users> findByIdAndActiveTrue(UUID id);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsernameAndIdNot(String username, UUID id);

    boolean existsByEmailAndIdNot(String email, UUID id);

    Page<Users> findByActiveTrue(Pageable pageable);

    Page<Users> findByActiveTrueAndUsernameContainingIgnoreCase(String username, Pageable pageable);

    List<Users> findByRoleAndActiveTrue(UserRole role);

    @Modifying
    @Query("update Users u set u.active = false where u.id = :id")
    void deactivateById(@Param("id") UUID id);
}
