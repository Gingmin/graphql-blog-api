package com.example.graphql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.example.follow.application.FollowService;
import com.example.graphql.user.UserMutationController;
import com.example.graphql.user.UserQueryController;
import com.example.user.application.UserService;
import org.junit.jupiter.api.Test;

class FollowControllerUnitTest {

  @Test
  void followUser_withMyId_callsService() {
    UserService userService = mock(UserService.class);
    FollowService followService = mock(FollowService.class);
    UserMutationController controller = new UserMutationController(userService, followService);

    given(followService.follow(1L, 2L)).willReturn(true);

    boolean ok = controller.followUser(1L, "2");
    assertThat(ok).isTrue();
  }

  @Test
  void followUser_withoutMyId_throws() {
    UserService userService = mock(UserService.class);
    FollowService followService = mock(FollowService.class);
    UserMutationController controller = new UserMutationController(userService, followService);

    assertThatThrownBy(() -> controller.followUser(null, "2"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("auth required");
  }

  @Test
  void isFollowing_withMyId_callsService() {
    UserService userService = mock(UserService.class);
    FollowService followService = mock(FollowService.class);
    UserQueryController controller = new UserQueryController(userService, followService);

    given(followService.isFollowing(1L, 2L)).willReturn(true);

    boolean ok = controller.isFollowing(1L, "2");
    assertThat(ok).isTrue();
  }
}

