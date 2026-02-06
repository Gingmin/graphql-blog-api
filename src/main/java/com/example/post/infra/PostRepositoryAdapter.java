package com.example.post.infra;

import com.example.post.application.PostRepository;
import com.example.post.domain.Post;
import com.example.post.infra.jpa.PostJpaEntity;
import com.example.post.infra.jpa.PostJpaRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class PostRepositoryAdapter implements PostRepository {
    private final PostJpaRepository jpaRepository;
    private final EntityManager entityManager;

    public PostRepositoryAdapter(PostJpaRepository jpaRepository, EntityManager entityManager) {
        this.jpaRepository = jpaRepository;
        this.entityManager = entityManager;
    }

    @Override
    public Post createPost(String title, String content, Long authorId, List<String> tagNames) {
        var saved = jpaRepository.save(new PostJpaEntity(title, content, authorId));
        syncTags(saved.getId(), tagNames);
        return toDomain(saved, loadTagNames(saved.getId()));
    }

    @Override
    public Post modifyPost(Long id, String title, String content, List<String> tagNames) {
        var entity =
            jpaRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("post not found: " + id));
        entity.setTitle(title);
        entity.setContent(content);
        var saved = jpaRepository.save(entity);
        syncTags(saved.getId(), tagNames);
        return toDomain(saved, loadTagNames(saved.getId()));
    }

    @Override
    public boolean deletePost(Long id) {
        jpaRepository.deleteById(id);
        return true;
    }

    @Override
    public List<Post> findAll() {
        return jpaRepository.findAll().stream()
            .map(e -> toDomain(e, loadTagNames(e.getId())))
            .toList();
    }

    @Override
    public List<Post> findByTag(String tagName) {
        return jpaRepository.findByTagName(tagName).stream()
            .map(e -> toDomain(e, loadTagNames(e.getId())))
            .toList();
    }

    @Override
    public Optional<Post> findById(Long id) {
        return jpaRepository.findById(id).map(e -> toDomain(e, loadTagNames(e.getId())));
    }

    private static Post toDomain(PostJpaEntity e, List<String> tagNames) {
        return new Post(
            e.getId(),
            e.getTitle(),
            e.getContent(),
            e.getAuthorId(),
            tagNames,
            e.getCreatedAt(),
            e.getModifiedAt(),
            e.getLikesCount()
        );
    }

    private void syncTags(Long postId, List<String> tagNames) {
        // clear existing
        entityManager
            .createNativeQuery("delete from post_tags where post_id = :postId")
            .setParameter("postId", postId)
            .executeUpdate();

        if (tagNames == null) return;

        var normalized =
            tagNames.stream()
                .filter(s -> s != null && !s.isBlank())
                .map(String::trim)
                .distinct()
                .toList();

        for (var name : normalized) {
            // upsert tag
            entityManager
                .createNativeQuery("insert into tags(name) values (:name) on conflict (name) do nothing")
                .setParameter("name", name)
                .executeUpdate();

            var tagId =
                ((Number)
                        entityManager
                            .createNativeQuery("select id from tags where name = :name")
                            .setParameter("name", name)
                            .getSingleResult())
                    .longValue();

            entityManager
                .createNativeQuery(
                    "insert into post_tags(post_id, tag_id) values (:postId, :tagId) on conflict do nothing")
                .setParameter("postId", postId)
                .setParameter("tagId", tagId)
                .executeUpdate();
        }
    }

    private List<String> loadTagNames(Long postId) {
        @SuppressWarnings("unchecked")
        var rows =
            (List<String>)
                entityManager
                    .createNativeQuery(
                        """
                        select t.name
                        from tags t
                        join post_tags pt on pt.tag_id = t.id
                        where pt.post_id = :postId
                        order by t.name asc
                        """)
                    .setParameter("postId", postId)
                    .getResultList();
        return rows;
    }
}
