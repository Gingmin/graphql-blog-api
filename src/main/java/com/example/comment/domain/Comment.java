package com.example.comment.domain;

import java.time.Instant;

public record Comment(
    Long id, String content, Long authorId, Long postId, Instant createdAt, Instant modifiedAt) {}

