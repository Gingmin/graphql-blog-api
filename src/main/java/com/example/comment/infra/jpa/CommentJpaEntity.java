package com.example.comment.infra.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "comments")
public class CommentJpaEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String content;

  @Column(name = "author_id", nullable = false)
  private Long authorId;

  @Column(name = "post_id", nullable = false)
  private Long postId;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "modified_at", nullable = false)
  private Instant modifiedAt;

  protected CommentJpaEntity() {}

  public CommentJpaEntity(String content, Long authorId, Long postId) {
    this.content = content;
    this.authorId = authorId;
    this.postId = postId;
  }

  @PrePersist
  void prePersist() {
    if (createdAt == null) {
      createdAt = Instant.now();
    }
    if (modifiedAt == null) {
      modifiedAt = createdAt;
    }
  }

  @PreUpdate
  void preUpdate() {
    modifiedAt = Instant.now();
  }

  public Long getId() {
    return id;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Long getAuthorId() {
    return authorId;
  }

  public Long getPostId() {
    return postId;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getModifiedAt() {
    return modifiedAt;
  }
}

