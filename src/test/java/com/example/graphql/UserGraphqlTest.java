package com.example.graphql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.example.graphql.user.UserMutationController;
import com.example.graphql.user.UserQueryController;
import com.example.follow.application.FollowService;
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
  @MockitoBean FollowService followService;

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

  @Test
  void followersCount_returnsInt() {
    given(followService.followersCount(2L)).willReturn(7L);

    graphQlTester
        .document("query($userId: ID!){ followersCount(userId:$userId) }")
        .variable("userId", "2")
        .execute()
        .path("followersCount")
        .entity(Integer.class)
        .satisfies(v -> assertThat(v).isEqualTo(7));
  }

  @Test
  void followingCount_returnsInt() {
    given(followService.followingCount(2L)).willReturn(3L);

    graphQlTester
        .document("query($userId: ID!){ followingCount(userId:$userId) }")
        .variable("userId", "2")
        .execute()
        .path("followingCount")
        .entity(Integer.class)
        .satisfies(v -> assertThat(v).isEqualTo(3));
  }

  @Test
  void isFollowing_withoutToken_returnsFalse() {
    graphQlTester
        .document("query($userId: ID!){ isFollowing(userId:$userId) }")
        .variable("userId", "2")
        .execute()
        .path("isFollowing")
        .entity(Boolean.class)
        .satisfies(v -> assertThat(v).isFalse());
  }

  @Test
  void followUser_withoutToken_returnsError() {
    graphQlTester
        .document("mutation($userId: ID!){ followUser(userId:$userId) }")
        .variable("userId", "2")
        .execute()
        .errors()
        .satisfy(errs -> assertThat(errs).isNotEmpty());
  }

  @Test
  void unfollowUser_withoutToken_returnsError() {
    graphQlTester
        .document("mutation($userId: ID!){ unfollowUser(userId:$userId) }")
        .variable("userId", "2")
        .execute()
        .errors()
        .satisfy(errs -> assertThat(errs).isNotEmpty());
  }
}

