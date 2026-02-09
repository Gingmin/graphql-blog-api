package com.example.graphql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.example.graphql.post.PostMutationController;
import com.example.post.application.PostService;
import org.junit.jupiter.api.Test;

class PostLikeControllerUnitTest {

  @Test
  void likePost_withMyId_returnsCount() {
    PostService postService = mock(PostService.class);
    PostMutationController controller = new PostMutationController(postService);

    given(postService.likePost(1L, 123L)).willReturn(5);

    int count = controller.likePost(123L, "1");
    assertThat(count).isEqualTo(5);
  }

  @Test
  void likePost_withoutMyId_throws() {
    PostService postService = mock(PostService.class);
    PostMutationController controller = new PostMutationController(postService);

    assertThatThrownBy(() -> controller.likePost(null, "1"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("auth required");
  }
}

