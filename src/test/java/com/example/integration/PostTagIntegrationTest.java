package com.example.integration;

import static org.assertj.core.api.Assertions.assertThat;

import javax.sql.DataSource;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@AutoConfigureGraphQlTester
@ActiveProfiles("test")
@Tag("integration")
class PostTagIntegrationTest extends IntegrationTestSupport {
  @Autowired GraphQlTester graphQlTester;

  PostTagIntegrationTest(@Autowired DataSource dataSource) {
    super(dataSource);
  }

  @Test
  void createPost_with_tags_then_postsByTag_returns_it() {
    // sign up for author id
    var signUp =
        graphQlTester
            .document(
                """
                mutation {
                  signUp(username:"author", email:"author@mk.com", password:"1234") {
                    user { id }
                    accessToken
                  }
                }
                """)
            .execute();

    var authorId = signUp.path("signUp.user.id").entity(String.class).get();

    graphQlTester
        .document(
            """
            mutation($title:String!, $content:String!, $authorId:ID!, $tagNames:[String!]) {
              createPost(title:$title, content:$content, authorId:$authorId, tagNames:$tagNames) {
                id
                tagNames
              }
            }
            """)
        .variable("title", "t1")
        .variable("content", "c1")
        .variable("authorId", authorId)
        .variable("tagNames", java.util.List.of("java", "graphql"))
        .execute()
        .path("createPost.tagNames")
        .entityList(String.class)
        .satisfies(tags -> assertThat(tags).contains("java", "graphql"));

    graphQlTester
        .document(
            """
            query($tagName:String!) {
              postsByTag(tagName:$tagName) { id title tagNames }
            }
            """)
        .variable("tagName", "java")
        .execute()
        .path("postsByTag[0].title")
        .entity(String.class)
        .satisfies(v -> assertThat(v).isEqualTo("t1"));
  }
}

