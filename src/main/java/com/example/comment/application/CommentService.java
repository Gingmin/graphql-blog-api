package com.example.comment.application;

import com.example.comment.domain.Comment;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentService {
  private final CommentRepository commentRepository;

  public CommentService(CommentRepository commentRepository) {
    this.commentRepository = commentRepository;
  }

  @Transactional(readOnly = true)
  public List<Comment> commentsByPost(long postId) {
    return commentRepository.findByPostId(postId);
  }

  @Transactional
  public Comment createComment(long postId, long authorId, String content) {
    if (content == null || content.isBlank()) {
      throw new IllegalArgumentException("content must not be blank");
    }
    return commentRepository.create(postId, authorId, content.trim());
  }

  @Transactional
  public Comment modifyComment(long id, long authorId, String content) {
    if (content == null || content.isBlank()) {
      throw new IllegalArgumentException("content must not be blank");
    }
    commentRepository
        .findByIdAndAuthorId(id, authorId)
        .orElseThrow(() -> new IllegalArgumentException("comment not found or not owner: " + id));
    return commentRepository.updateContent(id, content.trim());
  }

  @Transactional
  public boolean deleteComment(long id, long authorId) {
    commentRepository
        .findByIdAndAuthorId(id, authorId)
        .orElseThrow(() -> new IllegalArgumentException("comment not found or not owner: " + id));
    return commentRepository.delete(id);
  }
}

