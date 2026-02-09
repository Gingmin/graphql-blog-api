package com.example.graphql.comment;

import com.example.auth.AuthContext;
import com.example.comment.application.CommentService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
public class CommentMutationController {
  private final CommentService commentService;

  public CommentMutationController(CommentService commentService) {
    this.commentService = commentService;
  }

  @MutationMapping
  public CommentGql createComment(
      @ContextValue(name = AuthContext.USER_ID, required = false) Long myId,
      @Argument("postId") String postId,
      @Argument("content") String content) {
    if (myId == null) {
      throw new IllegalArgumentException("auth required");
    }
    return CommentMapper.toGql(commentService.createComment(Long.parseLong(postId), myId, content));
  }

  @MutationMapping
  public CommentGql modifyComment(
      @ContextValue(name = AuthContext.USER_ID, required = false) Long myId,
      @Argument("id") String id,
      @Argument("content") String content) {
    if (myId == null) {
      throw new IllegalArgumentException("auth required");
    }
    return CommentMapper.toGql(commentService.modifyComment(Long.parseLong(id), myId, content));
  }

  @MutationMapping
  public boolean deleteComment(
      @ContextValue(name = AuthContext.USER_ID, required = false) Long myId,
      @Argument("id") String id) {
    if (myId == null) {
      throw new IllegalArgumentException("auth required");
    }
    return commentService.deleteComment(Long.parseLong(id), myId);
  }
}

