package com.example.post.infra.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PostTagId implements Serializable {
  @Column(name = "post_id")
  private Long postId;

  @Column(name = "tag_id")
  private Long tagId;

  protected PostTagId() {}

  public PostTagId(Long postId, Long tagId) {
    this.postId = postId;
    this.tagId = tagId;
  }

  public Long getPostId() {
    return postId;
  }

  public Long getTagId() {
    return tagId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof PostTagId that)) return false;
    return Objects.equals(postId, that.postId) && Objects.equals(tagId, that.tagId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(postId, tagId);
  }
}

