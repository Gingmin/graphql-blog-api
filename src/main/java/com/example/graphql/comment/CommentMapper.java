package com.example.graphql.comment;

import com.example.comment.domain.Comment;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

final class CommentMapper {
  private CommentMapper() {}

  private static final DateTimeFormatter ISO_INSTANT =
      DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC);

  static CommentGql toGql(Comment c) {
    return new CommentGql(
        String.valueOf(c.id()),
        c.content(),
        String.valueOf(c.authorId()),
        String.valueOf(c.postId()),
        c.createdAt() == null ? null : ISO_INSTANT.format(c.createdAt()),
        c.modifiedAt() == null ? null : ISO_INSTANT.format(c.modifiedAt()));
  }
}

