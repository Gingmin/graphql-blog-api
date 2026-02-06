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
  public AuthPayloadGql signUp(
      @Argument("username") String username,
      @Argument("email") String email,
      @Argument("password") String password) {
    var result = userService.signUp(username, email, password);
    return new AuthPayloadGql(UserMapper.toGql(result.user()), result.accessToken());
  }

  @MutationMapping
  public AuthPayloadGql login(
      @Argument("email") String email, @Argument("password") String password) {
    var result = userService.login(email, password);
    return new AuthPayloadGql(UserMapper.toGql(result.user()), result.accessToken());
  }
}

