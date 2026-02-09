package com.example.auth;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.user.domain.User;
import org.junit.jupiter.api.Test;

class JwtServiceTest {
  @Test
  void issue_and_verify_roundTrip() {
    var jwtService = new JwtService("secret", "issuer", 3600);
    var user = new User(123L, "mkk", "mkk@example.com", null, null);

    var token = jwtService.issue(user);
    var userId = jwtService.verifyAndGetUserId(token);

    assertThat(userId).isEqualTo(123L);
  }
}

