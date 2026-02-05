package com.example.graphql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.example.graphql.user.UserMutationController;
import com.example.graphql.user.UserQueryController;
import com.example.user.application.UserService;
import com.example.user.domain.User;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@GraphQlTest({UserQueryController.class, UserMutationController.class})
class HelloGraphqlTest {
  @Autowired GraphQlTester graphQlTester;

  @Test
  void users_returnsList() {
    given(userService.users()).willReturn(List.of(new User(1L, "mkk", "mkk@example.com")));

    graphQlTester
        .document("query{ users { id username email } }")
        .execute()
        .path("users[0].username")
        .entity(String.class)
        .satisfies(v -> assertThat(v).isEqualTo("mkk"));
  }

  @Test
  void createUser_returnsCreatedUser() {
    given(userService.createUser("mkk", "mkk@example.com"))
        .willReturn(new User(1L, "mkk", "mkk@example.com"));

    graphQlTester
        .document("mutation{ createUser(username:\"mkk\", email:\"mkk@example.com\"){ id username email } }")
        .execute()
        .path("createUser.email")
        .entity(String.class)
        .satisfies(v -> assertThat(v).isEqualTo("mkk@example.com"));
  }

  @MockitoBean UserService userService;
}

