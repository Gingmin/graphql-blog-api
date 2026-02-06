package com.example.graphql.post;

import java.util.List;

public record PostGql(
    String id,
    String title,
    String content,
    String authorId,
    List<String> tagNames,
    String createdAt,
    String modifiedAt,
    int likes
) {}
