package com.example.graphql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.example.graphql.user.UserMutationController;
import com.example.graphql.user.UserQueryController;
import com.example.user.application.UserService;
import com.example.user.domain.User;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@GraphQlTest({UserQueryController.class, UserMutationController.class})
class UserGraphqlTest {
  @Autowired GraphQlTester graphQlTester;

  @MockitoBean UserService userService;

  @Test
  void users_returnsList() {
    given(userService.users())
        .willReturn(
            List.of(new User(1L, "mkk", "mkk@example.com", Instant.EPOCH, Instant.EPOCH)));

    graphQlTester
        .document("query { users { id username email createdAt modifiedAt } }")
        .execute()
        .path("users[0].username")
        .entity(String.class)
        .satisfies(v -> assertThat(v).isEqualTo("mkk"));
  }

  @Test
  void signUp_returnsAuthPayload() {
    given(userService.signUp("mkk", "mkk@example.com", "1234"))
        .willReturn(
            new UserService.AuthResult(
                new User(1L, "mkk", "mkk@example.com", Instant.EPOCH, Instant.EPOCH), "token-1"));

    graphQlTester
        .document(
            """
            mutation SignUp($username: String!, $email: String!, $password: String!) {
              signUp(username: $username, email: $email, password: $password) {
                accessToken
                user { id username email createdAt modifiedAt }
              }
            }
            """)
        .variable("username", "mkk")
        .variable("email", "mkk@example.com")
        .variable("password", "1234")
        .execute()
        .path("signUp.accessToken")
        .entity(String.class)
        .satisfies(v -> assertThat(v).isEqualTo("token-1"));
  }

  @Test
  void login_returnsAuthPayload() {
    given(userService.login("mkk@example.com", "1234"))
        .willReturn(
            new UserService.AuthResult(
                new User(1L, "mkk", "mkk@example.com", Instant.EPOCH, Instant.EPOCH), "token-2"));

    graphQlTester
        .document(
            """
            mutation Login($email: String!, $password: String!) {
              login(email: $email, password: $password) {
                accessToken
                user { id username email }
              }
            }
            """)
        .variable("email", "mkk@example.com")
        .variable("password", "1234")
        .execute()
        .path("login.user.email")
        .entity(String.class)
        .satisfies(v -> assertThat(v).isEqualTo("mkk@example.com"));
  }

  @Test
  void me_withoutToken_returnsNull() {
    graphQlTester
        .document("query { me { id username } }")
        .execute()
        .path("me")
        .valueIsNull();
  }
}

