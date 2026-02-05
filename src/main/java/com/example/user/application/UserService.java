package com.example.user.application;

import com.example.user.domain.User;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Transactional(readOnly = true)
  public List<User> users() {
    return userRepository.findAll();
  }

  @Transactional(readOnly = true)
  public User user(Long id) {
    return userRepository
        .findById(id)
        .orElseThrow(() -> new IllegalArgumentException("user not found: " + id));
  }

  @Transactional
  public User createUser(String username, String email) {
    if (username == null || username.isBlank()) {
      throw new IllegalArgumentException("username must not be blank");
    }
    if (email == null || email.isBlank()) {
      throw new IllegalArgumentException("email must not be blank");
    }
    if (userRepository.existsByUsername(username)) {
      throw new IllegalArgumentException("username already exists: " + username);
    }
    if (userRepository.existsByEmail(email)) {
      throw new IllegalArgumentException("email already exists: " + email);
    }

    return userRepository.save(new User(null, username, email));
  }
}

