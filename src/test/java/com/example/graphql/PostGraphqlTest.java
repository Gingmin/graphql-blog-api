package com.example.graphql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;

import com.example.graphql.post.PostMutationController;
import com.example.graphql.post.PostQueryController;
import com.example.post.application.PostPage;
import com.example.post.application.PostService;
import com.example.post.domain.Post;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@GraphQlTest({PostQueryController.class, PostMutationController.class})
class PostGraphqlTest {
  @Autowired GraphQlTester graphQlTester;

  @MockitoBean PostService postService;

  private static Post samplePost(long id) {
    return new Post(
        id,
        "t-" + id,
        "c-" + id,
        1L,
        List.of("java", "graphql"),
        Instant.EPOCH,
        Instant.EPOCH,
        0);
  }

  @Test
  void posts_returnsPage() {
    given(postService.posts(any(), any()))
        .willReturn(new PostPage(List.of(samplePost(1)), 0, 20, 1L, 1, false, false));

    graphQlTester
        .document(
            "query { posts { items { id title authorId tagNames likes } pageInfo { page size totalElements totalPages hasNext hasPrev } } }")
        .execute()
        .path("posts.items[0].title")
        .entity(String.class)
        .satisfies(v -> assertThat(v).isEqualTo("t-1"));
  }

  @Test
  void posts_withPagingArgs_returnsPage() {
    given(postService.posts(1, 5))
        .willReturn(new PostPage(List.of(samplePost(3)), 1, 5, 6L, 2, false, true));

    graphQlTester
        .document(
            "query($page:Int!, $size:Int!){ posts(page:$page, size:$size) { items { id } pageInfo { page size totalElements totalPages } } }")
        .variable("page", 1)
        .variable("size", 5)
        .execute()
        .path("posts.items[0].id")
        .entity(String.class)
        .satisfies(v -> assertThat(v).isEqualTo("3"));
  }

  @Test
  void post_returnsOne() {
    given(postService.post(1L)).willReturn(samplePost(1));

    graphQlTester
        .document("query($id: ID!){ post(id:$id){ id title authorId } }")
        .variable("id", "1")
        .execute()
        .path("post.authorId")
        .entity(String.class)
        .satisfies(v -> assertThat(v).isEqualTo("1"));
  }

  @Test
  void postsByTag_returnsList() {
    given(postService.postsByTag("java")).willReturn(List.of(samplePost(2)));

    graphQlTester
        .document("query($tagName:String!){ postsByTag(tagName:$tagName){ id title } }")
        .variable("tagName", "java")
        .execute()
        .path("postsByTag[0].id")
        .entity(String.class)
        .satisfies(v -> assertThat(v).isEqualTo("2"));
  }

  @Test
  void createPost_returnsPost() {
    given(postService.createPost("t", "c", 1L, List.of("java"))).willReturn(samplePost(10));

    graphQlTester
        .document(
            """
            mutation($title:String!, $content:String!, $authorId:ID!, $tagNames:[String!]) {
              createPost(title:$title, content:$content, authorId:$authorId, tagNames:$tagNames) {
                id title authorId tagNames
              }
            }
            """)
        .variable("title", "t")
        .variable("content", "c")
        .variable("authorId", "1")
        .variable("tagNames", List.of("java"))
        .execute()
        .path("createPost.id")
        .entity(String.class)
        .satisfies(v -> assertThat(v).isEqualTo("10"));
  }

  @Test
  void modifyPost_returnsPost() {
    given(postService.modifyPost(10L, "t2", "c2", List.of("graphql"))).willReturn(samplePost(10));

    graphQlTester
        .document(
            """
            mutation($id:ID!, $title:String!, $content:String!, $tagNames:[String!]) {
              modifyPost(id:$id, title:$title, content:$content, tagNames:$tagNames) {
                id
              }
            }
            """)
        .variable("id", "10")
        .variable("title", "t2")
        .variable("content", "c2")
        .variable("tagNames", List.of("graphql"))
        .execute()
        .path("modifyPost.id")
        .entity(String.class)
        .satisfies(v -> assertThat(v).isEqualTo("10"));
  }

  @Test
  void deletePost_returnsBoolean() {
    given(postService.deletePost(10L)).willReturn(true);

    graphQlTester
        .document("mutation($id:ID!){ deletePost(id:$id) }")
        .variable("id", "10")
        .execute()
        .path("deletePost")
        .entity(Boolean.class)
        .satisfies(v -> assertThat(v).isTrue());
  }

  @Test
  void likePost_withoutToken_returnsError() {
    graphQlTester
        .document("mutation($id: ID!){ likePost(id:$id) }")
        .variable("id", "1")
        .execute()
        .errors()
        .satisfy(errs -> assertThat(errs).isNotEmpty());
  }
}

