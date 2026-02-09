package com.example.post.infra.jpa;

import com.example.tag.infra.jpa.TagJpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "post_tags")
public class PostTagJpaEntity {
  @EmbeddedId private PostTagId id;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("postId")
  @JoinColumn(name = "post_id", nullable = false)
  private PostJpaEntity post;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("tagId")
  @JoinColumn(name = "tag_id", nullable = false)
  private TagJpaEntity tag;

  // DB default now()
  @Column(name = "created_at", insertable = false, updatable = false)
  private Instant createdAt;

  protected PostTagJpaEntity() {}

  public PostTagJpaEntity(PostJpaEntity post, TagJpaEntity tag) {
    this.post = post;
    this.tag = tag;
    this.id = new PostTagId(post.getId(), tag.getId());
  }

  public PostTagId getId() {
    return id;
  }

  public PostJpaEntity getPost() {
    return post;
  }

  public TagJpaEntity getTag() {
    return tag;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}

