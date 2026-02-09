package com.example.graphql.comment;

public record CommentGql(
    String id,
    String content,
    String authorId,
    String postId,
    String createdAt,
    String modifiedAt) {}

