package com.tqp.cms.repository;

import com.tqp.cms.entity.Users;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, UUID> {
    Optional<Users> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
