package com.example.comment.application;

import com.example.comment.domain.Comment;
import java.util.List;
import java.util.Optional;

public interface CommentRepository {
  Comment create(long postId, long authorId, String content);

  List<Comment> findByPostId(long postId);

  Optional<Comment> findById(long id);

  Optional<Comment> findByIdAndAuthorId(long id, long authorId);

  Comment updateContent(long id, String content);

  boolean delete(long id);
}

