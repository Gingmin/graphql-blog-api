package com.example.graphql.post;

import com.example.post.application.PostService;
import java.util.List;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class PostQueryController {
    private final PostService postService;

    public PostQueryController(PostService postService) {
        this.postService = postService;
    }

    @QueryMapping
    public List<PostGql> posts() {
        return postService.posts().stream().map(PostMapper::toGql).toList();
    }

    @QueryMapping
    public PostGql post(@Argument("id") String id) {
        return PostMapper.toGql(postService.post(Long.parseLong(id)));
    }

    @QueryMapping
    public List<PostGql> postsByTag(@Argument("tagName") String tagName) {
        return postService.postsByTag(tagName).stream().map(PostMapper::toGql).toList();
    }
}
