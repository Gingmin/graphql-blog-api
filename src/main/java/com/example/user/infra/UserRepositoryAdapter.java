package com.example.user.infra;

import com.example.user.application.UserRepository;
import com.example.user.domain.User;
import com.example.user.infra.jpa.UserJpaEntity;
import com.example.user.infra.jpa.UserJpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryAdapter implements UserRepository {
  private final UserJpaRepository jpaRepository;

  public UserRepositoryAdapter(UserJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public Optional<User> findById(Long id) {
    return jpaRepository.findById(id).map(UserRepositoryAdapter::toDomain);
  }

  @Override
  public List<User> findAll() {
    return jpaRepository.findAll().stream().map(UserRepositoryAdapter::toDomain).toList();
  }

  @Override
  public boolean existsByUsername(String username) {
    return jpaRepository.existsByUsername(username);
  }

  @Override
  public boolean existsByEmail(String email) {
    return jpaRepository.existsByEmail(email);
  }

  @Override
  public User create(String username, String email, String passwordHash) {
    var saved = jpaRepository.save(new UserJpaEntity(username, email, passwordHash));
    return toDomain(saved);
  }

  @Override
  public Optional<UserAuth> findAuthByEmail(String email) {
    return jpaRepository.findByEmail(email).map(e -> new UserAuth(toDomain(e), e.getPasswordHash()));
  }

  private static User toDomain(UserJpaEntity e) {
    return new User(e.getId(), e.getUsername(), e.getEmail(), e.getCreatedAt(), e.getModifiedAt());
  }
}

