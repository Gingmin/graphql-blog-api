package com.example.user.infra.jpa;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {
  boolean existsByUsername(String username);

  boolean existsByEmail(String email);

  Optional<UserJpaEntity> findByUsername(String username);

  Optional<UserJpaEntity> findByEmail(String email);
}

