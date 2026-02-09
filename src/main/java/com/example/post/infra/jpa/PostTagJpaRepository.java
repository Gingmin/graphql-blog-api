package com.example.post.infra.jpa;

import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostTagJpaRepository extends JpaRepository<PostTagJpaEntity, PostTagId> {
  @Query(
      """
      select pt
        from PostTagJpaEntity pt
        join fetch pt.tag t
       where pt.post.id in :postIds
      """)
  List<PostTagJpaEntity> findByPostIdInWithTag(@Param("postIds") Collection<Long> postIds);
}

