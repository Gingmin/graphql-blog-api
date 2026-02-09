package com.example.follow.infra.jpa;

import com.example.user.infra.jpa.UserJpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "follows")
public class FollowJpaEntity {
  @EmbeddedId private FollowId id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @MapsId("followerId")
  @JoinColumn(name = "follower_id", nullable = false)
  private UserJpaEntity follower;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @MapsId("followingId")
  @JoinColumn(name = "following_id", nullable = false)
  private UserJpaEntity following;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  protected FollowJpaEntity() {}

  public FollowJpaEntity(UserJpaEntity follower, UserJpaEntity following) {
    this.follower = follower;
    this.following = following;
    this.id = new FollowId(follower.getId(), following.getId());
  }

  @PrePersist
  void prePersist() {
    if (createdAt == null) {
      createdAt = Instant.now();
    }
  }

  public FollowId getId() {
    return id;
  }

  public UserJpaEntity getFollower() {
    return follower;
  }

  public UserJpaEntity getFollowing() {
    return following;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}

