package com.example.comment.infra.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentJpaRepository extends JpaRepository<CommentJpaEntity, Long> {
  List<CommentJpaEntity> findByPostIdOrderByCreatedAtDesc(long postId);

  Optional<CommentJpaEntity> findByIdAndAuthorId(long id, long authorId);
}

