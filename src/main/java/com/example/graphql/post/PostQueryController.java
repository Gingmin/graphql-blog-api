package com.example.graphql.post;

import com.example.auth.AuthContext;
import com.example.post.application.PostService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class PostQueryController {
    private final PostService postService;

    public PostQueryController(PostService postService) {
        this.postService = postService;
    }

    @QueryMapping
    public PostPageGql posts(@Argument("page") Integer page, @Argument("size") Integer size) {
        var result = postService.posts(page, size);
        int totalElements =
            result.totalElements() > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) result.totalElements();
        return new PostPageGql(
            result.items().stream().map(PostMapper::toGql).toList(),
            new PageInfoGql(
                result.page(),
                result.size(),
                totalElements,
                result.totalPages(),
                result.hasNext(),
                result.hasPrev()));
    }

    @QueryMapping
    public PostGql post(@Argument("id") String id) {
        return PostMapper.toGql(postService.post(Long.parseLong(id)));
    }

    @QueryMapping
    public java.util.List<PostGql> postsByTag(@Argument("tagName") String tagName) {
        return postService.postsByTag(tagName).stream().map(PostMapper::toGql).toList();
    }

    @QueryMapping
    public boolean hasLikedPost(
        @ContextValue(name = AuthContext.USER_ID, required = false) Long myId,
        @Argument("id") String id
    ) {
        if (myId == null) return false;
        return postService.hasLikedPost(Long.parseLong(id), myId);
    }
}
