package com.example.graphql.post;

import java.util.List;

public record PostPageGql(List<PostGql> items, PageInfoGql pageInfo) {}

