package com.example.post.infra.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import com.example.tag.infra.jpa.TagJpaEntity;

@Entity
@Table(name = "posts")
public class PostJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;
    
    @Column(name = "author_id", nullable = false)
    private Long authorId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "modified_at", nullable = false)
    private Instant modifiedAt;

    @Column(name = "likes_count", nullable = false)
    private int likesCount;

    @OneToMany(
        mappedBy = "post",
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true)
    private Set<PostTagJpaEntity> postTags = new LinkedHashSet<>();

    protected PostJpaEntity() {}

    public PostJpaEntity(String title, String content, Long authorId) {
        this.title = title;
        this.content = content;
        this.authorId = authorId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Set<PostTagJpaEntity> getPostTags() {
        return postTags;
    }

    /** Replace tags for this post. Requires post/tag ids to be non-null. */
    public void replaceTags(Set<TagJpaEntity> tags) {
        postTags.clear();
        if (tags == null) {
            return;
        }
        for (var tag : tags) {
        postTags.add(new PostTagJpaEntity(this, tag));
        }
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

    public String getTitle() {
        return title;
    }
    
    public String getContent() {
        return content;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
    
    public Instant getModifiedAt() {
        return modifiedAt;
    }

    public int getLikesCount() {
        return likesCount;
    }
}
