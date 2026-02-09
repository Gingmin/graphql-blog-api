package com.example.follow.infra;

import com.example.follow.application.FollowRepository;
import com.example.follow.infra.jpa.FollowId;
import com.example.follow.infra.jpa.FollowJpaEntity;
import com.example.follow.infra.jpa.FollowJpaRepository;
import com.example.user.domain.User;
import com.example.user.infra.jpa.UserJpaEntity;
import com.example.user.infra.jpa.UserJpaRepository;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class FollowRepositoryAdapter implements FollowRepository {
  private final FollowJpaRepository followJpaRepository;
  private final UserJpaRepository userJpaRepository;

  public FollowRepositoryAdapter(FollowJpaRepository followJpaRepository, UserJpaRepository userJpaRepository) {
    this.followJpaRepository = followJpaRepository;
    this.userJpaRepository = userJpaRepository;
  }

  @Override
  public boolean follow(long followerId, long followingId) {
    if (followJpaRepository.existsByIdFollowerIdAndIdFollowingId(followerId, followingId)) {
      return false;
    }
    UserJpaEntity follower =
        userJpaRepository
            .findById(followerId)
            .orElseThrow(() -> new IllegalArgumentException("user not found: " + followerId));
    UserJpaEntity following =
        userJpaRepository
            .findById(followingId)
            .orElseThrow(() -> new IllegalArgumentException("user not found: " + followingId));
    followJpaRepository.save(new FollowJpaEntity(follower, following));
    return true;
  }

  @Override
  public boolean unfollow(long followerId, long followingId) {
    if (!followJpaRepository.existsByIdFollowerIdAndIdFollowingId(followerId, followingId)) {
      return false;
    }
    followJpaRepository.deleteByIdFollowerIdAndIdFollowingId(followerId, followingId);
    return true;
  }

  @Override
  public long followersCount(long userId) {
    return followJpaRepository.countByIdFollowingId(userId);
  }

  @Override
  public long followingCount(long userId) {
    return followJpaRepository.countByIdFollowerId(userId);
  }

  @Override
  public List<User> followers(long userId) {
    return followJpaRepository.findFollowers(userId).stream().map(FollowRepositoryAdapter::toDomain).toList();
  }

  @Override
  public List<User> following(long userId) {
    return followJpaRepository.findFollowing(userId).stream().map(FollowRepositoryAdapter::toDomain).toList();
  }

  @Override
  public boolean isFollowing(long followerId, long followingId) {
    return followJpaRepository.existsByIdFollowerIdAndIdFollowingId(followerId, followingId);
  }

  private static User toDomain(UserJpaEntity e) {
    return new User(e.getId(), e.getUsername(), e.getEmail(), e.getCreatedAt(), e.getModifiedAt());
  }
}

