package com.example.post.infra.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostLikeJpaRepository extends JpaRepository<PostLikeJpaEntity, PostLikeId> {
  boolean existsByIdPostIdAndIdUserId(long postId, long userId);

  @Modifying
  @Query(
      value =
          """
          insert into post_likes (post_id, user_id, created_at)
          values (:postId, :userId, now())
          on conflict do nothing
          """,
      nativeQuery = true)
  int insertIgnore(@Param("postId") long postId, @Param("userId") long userId);
}

