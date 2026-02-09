package com.example.comment.infra;

import com.example.comment.application.CommentRepository;
import com.example.comment.domain.Comment;
import com.example.comment.infra.jpa.CommentJpaEntity;
import com.example.comment.infra.jpa.CommentJpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class CommentRepositoryAdapter implements CommentRepository {
  private final CommentJpaRepository commentJpaRepository;

  public CommentRepositoryAdapter(CommentJpaRepository commentJpaRepository) {
    this.commentJpaRepository = commentJpaRepository;
  }

  @Override
  public Comment create(long postId, long authorId, String content) {
    var saved = commentJpaRepository.save(new CommentJpaEntity(content, authorId, postId));
    return toDomain(saved);
  }

  @Override
  public List<Comment> findByPostId(long postId) {
    return commentJpaRepository.findByPostIdOrderByCreatedAtDesc(postId).stream()
        .map(CommentRepositoryAdapter::toDomain)
        .toList();
  }

  @Override
  public Optional<Comment> findById(long id) {
    return commentJpaRepository.findById(id).map(CommentRepositoryAdapter::toDomain);
  }

  @Override
  public Optional<Comment> findByIdAndAuthorId(long id, long authorId) {
    return commentJpaRepository.findByIdAndAuthorId(id, authorId).map(CommentRepositoryAdapter::toDomain);
  }

  @Override
  public Comment updateContent(long id, String content) {
    var entity =
        commentJpaRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("comment not found: " + id));
    entity.setContent(content);
    var saved = commentJpaRepository.save(entity);
    return toDomain(saved);
  }

  @Override
  public boolean delete(long id) {
    commentJpaRepository.deleteById(id);
    return true;
  }

  private static Comment toDomain(CommentJpaEntity e) {
    return new Comment(e.getId(), e.getContent(), e.getAuthorId(), e.getPostId(), e.getCreatedAt(), e.getModifiedAt());
  }
}

