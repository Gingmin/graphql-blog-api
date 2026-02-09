package com.example.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@AutoConfigureGraphQlTester
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Tag("integration")
class AuthIntegrationTest extends IntegrationTestSupport {
  @Autowired GraphQlTester graphQlTester;
  @Autowired MockMvc mockMvc;

  AuthIntegrationTest(@Autowired DataSource dataSource) {
    super(dataSource);
  }

  @Test
  void signUp_then_login_then_me_with_token() throws Exception {
    var signUp =
        graphQlTester
            .document(
                """
                mutation SignUp($username:String!, $email:String!, $password:String!) {
                  signUp(username:$username, email:$email, password:$password) {
                    accessToken
                    user { id username email }
                  }
                }
                """)
            .variable("username", "mk")
            .variable("email", "mk@mk.com")
            .variable("password", "1234")
            .execute();

    var token = signUp.path("signUp.accessToken").entity(String.class).get();
    assertThat(token).isNotBlank();

    graphQlTester
        .document(
            """
            mutation Login($email:String!, $password:String!) {
              login(email:$email, password:$password) {
                accessToken
                user { id email }
              }
            }
            """)
        .variable("email", "mk@mk.com")
        .variable("password", "1234")
        .execute()
        .path("login.user.email")
        .entity(String.class)
        .satisfies(v -> assertThat(v).isEqualTo("mk@mk.com"));

    graphQlTester
        .document("query { me { id username email } }")
        .execute()
        .path("me")
        .valueIsNull();

    // Send Authorization header via MockMvc to verify interceptor path
    mockMvc
        .perform(
            post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(
                    """
                    {"query":"query { me { id username email } }"}
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.me.username").value("mk"));
  }
}

