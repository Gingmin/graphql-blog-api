package com.example.user.domain;

import java.time.Instant;

public record User(Long id, String username, String email, Instant createdAt, Instant modifiedAt) {}

