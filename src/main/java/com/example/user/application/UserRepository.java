package com.example.user.application;

import com.example.user.domain.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
  Optional<User> findById(Long id);

  List<User> findAll();

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);

  User save(User user);
}

