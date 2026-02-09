package com.example.follow.application;

import com.example.user.domain.User;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FollowService {
  private final FollowRepository followRepository;

  public FollowService(FollowRepository followRepository) {
    this.followRepository = followRepository;
  }

  @Transactional(readOnly = true)
  public List<User> followers(long userId) {
    return followRepository.followers(userId);
  }

  @Transactional(readOnly = true)
  public List<User> following(long userId) {
    return followRepository.following(userId);
  }

  @Transactional(readOnly = true)
  public long followersCount(long userId) {
    return followRepository.followersCount(userId);
  }

  @Transactional(readOnly = true)
  public long followingCount(long userId) {
    return followRepository.followingCount(userId);
  }

  @Transactional
  public boolean follow(long followerId, long followingId) {
    if (followerId == followingId) {
      throw new IllegalArgumentException("cannot follow yourself");
    }
    return followRepository.follow(followerId, followingId);
  }

  @Transactional
  public boolean unfollow(long followerId, long followingId) {
    if (followerId == followingId) {
      return false;
    }
    return followRepository.unfollow(followerId, followingId);
  }

  @Transactional(readOnly = true)
  public boolean isFollowing(long followerId, long followingId) {
    if (followerId == followingId) return false;
    return followRepository.isFollowing(followerId, followingId);
  }
}

