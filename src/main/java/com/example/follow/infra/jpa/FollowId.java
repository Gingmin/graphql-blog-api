package com.example.follow.infra.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class FollowId implements Serializable {
  @Column(name = "follower_id", nullable = false)
  private Long followerId;

  @Column(name = "following_id", nullable = false)
  private Long followingId;

  protected FollowId() {}

  public FollowId(Long followerId, Long followingId) {
    this.followerId = followerId;
    this.followingId = followingId;
  }

  public Long getFollowerId() {
    return followerId;
  }

  public Long getFollowingId() {
    return followingId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    FollowId followId = (FollowId) o;
    return Objects.equals(followerId, followId.followerId)
        && Objects.equals(followingId, followId.followingId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(followerId, followingId);
  }
}

