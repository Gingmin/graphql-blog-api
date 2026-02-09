package com.example.graphql.comment;

import com.example.comment.application.CommentService;
import java.util.List;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class CommentQueryController {
  private final CommentService commentService;

  public CommentQueryController(CommentService commentService) {
    this.commentService = commentService;
  }

  @QueryMapping
  public List<CommentGql> commentsByPost(@Argument("postId") String postId) {
    return commentService.commentsByPost(Long.parseLong(postId)).stream().map(CommentMapper::toGql).toList();
  }
}

