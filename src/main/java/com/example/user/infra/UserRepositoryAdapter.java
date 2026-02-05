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
  public User save(User user) {
    var saved = jpaRepository.save(toEntity(user));
    return toDomain(saved);
  }

  private static User toDomain(UserJpaEntity e) {
    return new User(e.getId(), e.getUsername(), e.getEmail());
  }

  private static UserJpaEntity toEntity(User user) {
    // 신규 생성 케이스만 우선 지원 (id는 DB에서 생성)
    return new UserJpaEntity(user.username(), user.email());
  }
}

