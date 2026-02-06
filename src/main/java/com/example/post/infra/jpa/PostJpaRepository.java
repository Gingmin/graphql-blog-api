package com.example.post.infra.jpa;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostJpaRepository extends JpaRepository<PostJpaEntity, Long> {
    
    @Query(
        value =
            """
            select p.*
            from posts p
            join post_tags pt on pt.post_id = p.id
            join tags t on t.id = pt.tag_id
            where t.name = :tagName
            order by p.created_at desc
            """,
        nativeQuery = true)
    List<PostJpaEntity> findByTagName(@Param("tagName") String tagName);
}
