package com.example.graphql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.example.comment.application.CommentService;
import com.example.comment.domain.Comment;
import com.example.graphql.comment.CommentMutationController;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class CommentControllerUnitTest {

  @Test
  void createComment_withMyId_returnsGql() {
    CommentService commentService = mock(CommentService.class);
    CommentMutationController controller = new CommentMutationController(commentService);

    given(commentService.createComment(10L, 2L, "hi"))
        .willReturn(new Comment(1L, "hi", 2L, 10L, Instant.EPOCH, Instant.EPOCH));

    var gql = controller.createComment(2L, "10", "hi");
    assertThat(gql.authorId()).isEqualTo("2");
    assertThat(gql.postId()).isEqualTo("10");
  }

  @Test
  void createComment_withoutMyId_throws() {
    CommentService commentService = mock(CommentService.class);
    CommentMutationController controller = new CommentMutationController(commentService);

    assertThatThrownBy(() -> controller.createComment(null, "10", "hi"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("auth required");
  }
}

