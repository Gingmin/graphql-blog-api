package com.example.graphql.user;

import com.example.auth.AuthContext;
import com.example.user.application.UserService;
import java.util.List;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class UserQueryController {
  private final UserService userService;

  public UserQueryController(UserService userService) {
    this.userService = userService;
  }

  @QueryMapping
  public UserGql me(@ContextValue(name = AuthContext.USER_ID, required = false) Long userId) {
    if (userId == null) {
      return null; // schema: me: User (nullable)
    }
    return UserMapper.toGql(userService.user(userId));
  }

  @QueryMapping
  public List<UserGql> users() {
    return userService.users().stream().map(UserMapper::toGql).toList();
  }

  @QueryMapping
  public UserGql user(@Argument("id") String id) {
    return UserMapper.toGql(userService.user(Long.parseLong(id)));
  }
}

