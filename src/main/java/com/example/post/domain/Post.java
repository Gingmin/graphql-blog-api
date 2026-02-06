package com.example.post.domain;

import java.time.Instant;
import java.util.List;

public record Post(
    Long id,
    String title,
    String content,
    Long authorId,
    List<String> tagNames,
    Instant createdAt,
    Instant modifiedAt,
    int likesCount
) {}
