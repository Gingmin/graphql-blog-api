package com.example.graphql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.example.comment.application.CommentService;
import com.example.comment.domain.Comment;
import com.example.graphql.comment.CommentMutationController;
import com.example.graphql.comment.CommentQueryController;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@GraphQlTest({CommentQueryController.class, CommentMutationController.class})
class CommentGraphqlTest {
  @Autowired GraphQlTester graphQlTester;

  @MockitoBean CommentService commentService;

  private static Comment sample(long id, long postId, long authorId) {
    return new Comment(id, "c-" + id, authorId, postId, Instant.EPOCH, Instant.EPOCH);
  }

  @Test
  void commentsByPost_returnsList() {
    given(commentService.commentsByPost(10L)).willReturn(List.of(sample(1, 10, 2)));

    graphQlTester
        .document("query($postId: ID!){ commentsByPost(postId:$postId){ id content authorId postId } }")
        .variable("postId", "10")
        .execute()
        .path("commentsByPost[0].content")
        .entity(String.class)
        .satisfies(v -> assertThat(v).isEqualTo("c-1"));
  }

  @Test
  void createComment_withoutToken_returnsError() {
    graphQlTester
        .document("mutation($postId: ID!, $content:String!){ createComment(postId:$postId, content:$content){ id } }")
        .variable("postId", "10")
        .variable("content", "hi")
        .execute()
        .errors()
        .satisfy(errs -> assertThat(errs).isNotEmpty());
  }
}

