package com.example.post.infra.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostJpaRepository extends JpaRepository<PostJpaEntity, Long> {

    @Query("select p from PostJpaEntity p order by p.createdAt desc")
    Page<PostJpaEntity> findPage(Pageable pageable);
    
    @Query(
        """
        SELECT DISTINCT p
          from PostJpaEntity p
          LEFT JOIN FETCH p.postTags pt
          LEFT JOIN FETCH pt.tag t
         ORDER BY p.createdAt DESC
        """)
    List<PostJpaEntity> findAllWithTags();

    @Query(
        """
        SELECT p
          from PostJpaEntity p
          LEFT JOIN FETCH p.postTags pt
          LEFT JOIN FETCH pt.tag t
         WHERE p.id = :id
        """)
    Optional<PostJpaEntity> findByIdWithTags(@Param("id") Long id);

    @Query(
        """
        SELECT DISTINCT p
          from PostJpaEntity p
          JOIN p.postTags pt
          JOIN pt.tag t
         WHERE t.name = :tagName
         ORDER BY p.createdAt DESC
        """)
    List<PostJpaEntity> findByTagNameWithTags(@Param("tagName") String tagName);

    @Modifying
    @Query("update PostJpaEntity p set p.likesCount = p.likesCount + 1 where p.id = :id")
    int incrementLikes(@Param("id") Long id);

    @Query("select p.likesCount from PostJpaEntity p where p.id = :id")
    Integer findLikesCount(@Param("id") Long id);
}
