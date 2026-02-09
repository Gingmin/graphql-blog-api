package com.example.graphql.user;

import com.example.auth.AuthContext;
import com.example.follow.application.FollowService;
import com.example.user.application.UserService;
import java.util.List;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class UserQueryController {
  private final UserService userService;
  private final FollowService followService;

  public UserQueryController(UserService userService, FollowService followService) {
    this.userService = userService;
    this.followService = followService;
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

  @QueryMapping
  public List<UserGql> followers(@Argument("userId") String userId) {
    return followService.followers(Long.parseLong(userId)).stream().map(UserMapper::toGql).toList();
  }

  @QueryMapping
  public List<UserGql> following(@Argument("userId") String userId) {
    return followService.following(Long.parseLong(userId)).stream().map(UserMapper::toGql).toList();
  }

  @QueryMapping
  public int followersCount(@Argument("userId") String userId) {
    long v = followService.followersCount(Long.parseLong(userId));
    return v > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) v;
  }

  @QueryMapping
  public int followingCount(@Argument("userId") String userId) {
    long v = followService.followingCount(Long.parseLong(userId));
    return v > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) v;
  }

  @QueryMapping
  public boolean isFollowing(
      @ContextValue(name = AuthContext.USER_ID, required = false) Long myId,
      @Argument("userId") String userId) {
    if (myId == null) return false;
    return followService.isFollowing(myId, Long.parseLong(userId));
  }
}

