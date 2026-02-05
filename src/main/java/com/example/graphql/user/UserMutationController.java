package com.example.graphql.user;

import com.example.user.application.UserService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
public class UserMutationController {
  private final UserService userService;

  public UserMutationController(UserService userService) {
    this.userService = userService;
  }

  @MutationMapping
  public UserGql createUser(@Argument("username") String username, @Argument("email") String email) {
    return UserMapper.toGql(userService.createUser(username, email));
  }
}

