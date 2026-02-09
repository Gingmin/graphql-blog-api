package com.example.graphql.post;

public record PageInfoGql(
    int page,
    int size,
    int totalElements,
    int totalPages,
    boolean hasNext,
    boolean hasPrev) {}

