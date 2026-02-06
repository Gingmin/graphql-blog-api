package com.example.graphql.post;

import com.example.post.domain.Post;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class PostMapper {
    
    private PostMapper() {}

    private static final DateTimeFormatter ISO_INSTANT =
        DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC);

    static PostGql toGql(Post post) {
        return new PostGql(
            String.valueOf(post.id()),
            post.title(),
            post.content(),
        String.valueOf(post.authorId()),
        post.tagNames() == null ? java.util.List.of() : post.tagNames(),
        post.createdAt() == null ? null : ISO_INSTANT.format(post.createdAt()),
        post.modifiedAt() == null ? null : ISO_INSTANT.format(post.modifiedAt()),
        post.likesCount()
        );
    }
}
