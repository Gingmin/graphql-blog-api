package com.example.graphql.user;

import com.example.user.domain.User;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

final class UserMapper {
  private UserMapper() {}

  private static final DateTimeFormatter ISO_INSTANT =
      DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC);

  static UserGql toGql(User user) {
    return new UserGql(
        String.valueOf(user.id()),
        user.username(),
        user.email(),
        user.createdAt() == null ? null : ISO_INSTANT.format(user.createdAt()),
        user.modifiedAt() == null ? null : ISO_INSTANT.format(user.modifiedAt()));
  }
}

