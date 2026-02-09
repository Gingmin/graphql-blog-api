package com.example.post.infra;

import com.example.post.application.PostRepository;
import com.example.post.domain.Post;
import com.example.post.infra.jpa.PostJpaEntity;
import com.example.post.infra.jpa.PostJpaRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import com.example.tag.infra.jpa.TagJpaEntity;
import com.example.tag.infra.jpa.TagJpaRepository;

@Repository
public class PostRepositoryAdapter implements PostRepository {
    private final PostJpaRepository jpaRepository;
    private final TagJpaRepository tagJpaRepository;

    public PostRepositoryAdapter(PostJpaRepository jpaRepository, TagJpaRepository tagJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.tagJpaRepository = tagJpaRepository;
    }

    @Override
    public Post createPost(String title, String content, Long authorId, List<String> tagNames) {
        var saved = jpaRepository.save(new PostJpaEntity(title, content, authorId));
        var tags = loadOrCreateTags(tagNames);
        saved.replaceTags(tags);
        saved = jpaRepository.save(saved);
        return toDomain(saved);
    }

    @Override
    public Post modifyPost(Long id, String title, String content, List<String> tagNames) {
        var entity =
            jpaRepository
                .findByIdWithTags(id)
                .orElseThrow(() -> new IllegalArgumentException("post not found: " + id));
        entity.setTitle(title);
        entity.setContent(content);
        var tags = loadOrCreateTags(tagNames);
        entity.replaceTags(tags);
        var saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public boolean deletePost(Long id) {
        jpaRepository.deleteById(id);
        return true;
    }

    @Override
    public List<Post> findAll() {
        return jpaRepository.findAllWithTags().stream().map(PostRepositoryAdapter::toDomain).toList();
    }

    @Override
    public List<Post> findByTag(String tagName) {
        return jpaRepository.findByTagNameWithTags(tagName).stream()
            .map(PostRepositoryAdapter::toDomain)
            .toList();
    }

    @Override
    public Optional<Post> findById(Long id) {
        return jpaRepository.findByIdWithTags(id).map(PostRepositoryAdapter::toDomain);
    }

    private static Post toDomain(PostJpaEntity e) {
        var tagNames =
            e.getPostTags().stream()
                .map(pt -> pt.getTag().getName())
                .sorted()
                .toList();
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

    private Set<TagJpaEntity> loadOrCreateTags(List<String> tagNames) {
        if (tagNames == null) {
            return Set.of();
        }

        var normalized =
            tagNames.stream()
                .filter(s -> s != null && !s.isBlank())
                .map(String::trim)
                .distinct()
                .toList();
        if (normalized.isEmpty()) {
            return Set.of();
        }

        var existing = tagJpaRepository.findByNameIn(normalized);
        Map<String, TagJpaEntity> byName =
            existing.stream().collect(Collectors.toMap(TagJpaEntity::getName, t -> t));

        for (var name : normalized) {
            if (!byName.containsKey(name)) {
                var created = tagJpaRepository.save(new TagJpaEntity(name));
                byName.put(name, created);
            }
        }

        return normalized.stream().map(byName::get).collect(Collectors.toSet());
    }
}
