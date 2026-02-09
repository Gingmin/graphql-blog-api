package com.example.post.infra.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "post_likes")
public class PostLikeJpaEntity {
  @EmbeddedId private PostLikeId id;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  protected PostLikeJpaEntity() {}

  public PostLikeJpaEntity(Long postId, Long userId) {
    this.id = new PostLikeId(postId, userId);
  }

  @PrePersist
  void prePersist() {
    if (createdAt == null) {
      createdAt = Instant.now();
    }
  }

  public PostLikeId getId() {
    return id;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}

