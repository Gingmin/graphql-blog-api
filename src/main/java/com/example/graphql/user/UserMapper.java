package com.example.graphql.user;

import com.example.user.domain.User;

final class UserMapper {
  private UserMapper() {}

  static UserGql toGql(User user) {
    return new UserGql(String.valueOf(user.id()), user.username(), user.email());
  }
}

