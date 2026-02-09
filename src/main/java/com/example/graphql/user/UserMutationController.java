package com.example.graphql.user;

import com.example.auth.AuthContext;
import com.example.follow.application.FollowService;
import com.example.user.application.UserService;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
public class UserMutationController {
  private final UserService userService;
  private final FollowService followService;

  public UserMutationController(UserService userService, FollowService followService) {
    this.userService = userService;
    this.followService = followService;
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

  @MutationMapping
  public boolean followUser(
      @ContextValue(name = AuthContext.USER_ID, required = false) Long myId,
      @Argument("userId") String userId) {
    if (myId == null) {
      throw new IllegalArgumentException("auth required");
    }
    return followService.follow(myId, Long.parseLong(userId));
  }

  @MutationMapping
  public boolean unfollowUser(
      @ContextValue(name = AuthContext.USER_ID, required = false) Long myId,
      @Argument("userId") String userId) {
    if (myId == null) {
      throw new IllegalArgumentException("auth required");
    }
    return followService.unfollow(myId, Long.parseLong(userId));
  }
}

