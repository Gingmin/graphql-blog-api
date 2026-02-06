package com.example.graphql.post;

import com.example.post.application.PostService;
import java.util.List;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
public class PostMutationController {

    private final PostService postService;

    public PostMutationController(PostService postService) {
        this.postService = postService;
    }

    @MutationMapping
    public PostGql createPost(
        @Argument("title") String title,
        @Argument("content") String content, 
        @Argument("authorId") String authorId, 
        @Argument("tagNames") List<String> tagNames
    ) {
        return PostMapper.toGql(postService.createPost(title, content, Long.parseLong(authorId), tagNames));
    }

    @MutationMapping
    public PostGql modifyPost(
        @Argument("id") String id,
        @Argument("title") String title,
        @Argument("content") String content,
        @Argument("tagNames") List<String> tagNames
    ) {
        return PostMapper.toGql(postService.modifyPost(Long.parseLong(id), title, content, tagNames));
    }

    @MutationMapping
    public boolean deletePost(@Argument("id") String id) {
        return postService.deletePost(Long.parseLong(id));
    }
}
