package com.tqp.cms.repository;

import com.tqp.cms.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, UUID> {
    Optional<Users> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsernameAndIdNot(String username, UUID id);

    boolean existsByEmailAndIdNot(String email, UUID id);

    Page<Users> findByActiveTrue(Pageable pageable);

    Page<Users> findByActiveTrueAndUsernameContainingIgnoreCase(String username, Pageable pageable);
}
