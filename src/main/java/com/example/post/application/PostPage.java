package com.example.post.application;

import com.example.post.domain.Post;
import java.util.List;

public record PostPage(
    List<Post> items,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean hasNext,
    boolean hasPrev) {}

