package com.example.user.application;

import com.example.auth.JwtService;
import com.example.user.domain.User;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  public record AuthResult(User user, String accessToken) {}

  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
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
  public AuthResult signUp(String username, String email, String password) {
    if (username == null || username.isBlank()) {
      throw new IllegalArgumentException("username must not be blank");
    }
    if (email == null || email.isBlank()) {
      throw new IllegalArgumentException("email must not be blank");
    }
    if (password == null || password.isBlank()) {
      throw new IllegalArgumentException("password must not be blank");
    }
    if (userRepository.existsByUsername(username)) {
      throw new IllegalArgumentException("username already exists: " + username);
    }
    if (userRepository.existsByEmail(email)) {
      throw new IllegalArgumentException("email already exists: " + email);
    }

    var passwordHash = passwordEncoder.encode(password);
    var user = userRepository.create(username, email, passwordHash);
    var token = jwtService.issue(user);
    return new AuthResult(user, token);
  }

  @Transactional
  public AuthResult login(String email, String password) {
    if (email == null || email.isBlank()) {
      throw new IllegalArgumentException("email must not be blank");
    }
    if (password == null || password.isBlank()) {
      throw new IllegalArgumentException("password must not be blank");
    }

    var auth = userRepository.findAuthByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("invalid credentials"));

    if (!passwordEncoder.matches(password, auth.passwordHash())) {
      throw new IllegalArgumentException("invalid credentials");
    }

    var token = jwtService.issue(auth.user());
    return new AuthResult(auth.user(), token);
  }
}

