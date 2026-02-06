package com.example.auth;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import reactor.core.publisher.Mono;

/**
 * Reads Authorization header, verifies JWT, and stores user id into GraphQL context.
 *
 * <p>- No header: anonymous (context has no userId)
 * <p>- Invalid header/token: fails request with GraphQL error
 */
@Component
public class JwtWebGraphQlInterceptor implements WebGraphQlInterceptor {
  private final JwtService jwtService;

  public JwtWebGraphQlInterceptor(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  @Override
  public Mono<WebGraphQlResponse> intercept(WebGraphQlRequest request, Chain chain) {
    var auth = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
    var token = extractBearer(auth);

    if (!StringUtils.hasText(token)) {
      return chain.next(request);
    }

    long userId = jwtService.verifyAndGetUserId(token);

    request.configureExecutionInput(
        (executionInput, builder) ->
            builder
                .graphQLContext(
                    ctx -> {
                      ctx.put(AuthContext.USER_ID, userId);
                    })
                .build());

    return chain.next(request);
  }

  private static String extractBearer(String authorization) {
    if (!StringUtils.hasText(authorization)) return null;
    if (authorization.regionMatches(true, 0, "Bearer ", 0, 7)) {
      return authorization.substring(7).trim();
    }
    return null;
  }
}

