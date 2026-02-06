package com.example.user.application;

import com.example.user.domain.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
  record UserAuth(User user, String passwordHash) {}

  Optional<User> findById(Long id);

  List<User> findAll();

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);

  User create(String username, String email, String passwordHash);

  Optional<UserAuth> findAuthByEmail(String email);
}

