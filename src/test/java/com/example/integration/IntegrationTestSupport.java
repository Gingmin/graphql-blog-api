package com.example.integration;

import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.jdbc.core.JdbcTemplate;

/** Simple DB cleanup for local Postgres integration tests. */
abstract class IntegrationTestSupport {
  private final JdbcTemplate jdbc;

  protected IntegrationTestSupport(DataSource dataSource) {
    this.jdbc = new JdbcTemplate(dataSource);
  }

  @BeforeEach
  void cleanup() {
    // order matters due to FK constraints
    jdbc.execute("delete from post_likes");
    jdbc.execute("delete from post_tags");
    jdbc.execute("delete from tags");
    jdbc.execute("delete from comments");
    jdbc.execute("delete from follows");
    jdbc.execute("delete from posts");
    jdbc.execute("delete from users");
  }
}

