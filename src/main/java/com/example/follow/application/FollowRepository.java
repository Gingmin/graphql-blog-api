package com.example.follow.application;

import com.example.user.domain.User;
import java.util.List;

public interface FollowRepository {
  boolean follow(long followerId, long followingId);

  boolean unfollow(long followerId, long followingId);

  long followersCount(long userId);

  long followingCount(long userId);

  List<User> followers(long userId);

  List<User> following(long userId);

  boolean isFollowing(long followerId, long followingId);
}

