package com.example.post.infra;

import com.example.post.application.PostPage;
import com.example.post.application.PostRepository;
import com.example.post.domain.Post;
import com.example.post.infra.jpa.PostJpaEntity;
import com.example.post.infra.jpa.PostJpaRepository;
import com.example.post.infra.jpa.PostLikeJpaRepository;
import com.example.post.infra.jpa.PostTagJpaRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import com.example.tag.infra.jpa.TagJpaEntity;
import com.example.tag.infra.jpa.TagJpaRepository;
import org.springframework.data.domain.PageRequest;

@Repository
public class PostRepositoryAdapter implements PostRepository {
    private final PostJpaRepository jpaRepository;
    private final TagJpaRepository tagJpaRepository;
    private final PostLikeJpaRepository postLikeJpaRepository;
    private final PostTagJpaRepository postTagJpaRepository;

    public PostRepositoryAdapter(
        PostJpaRepository jpaRepository,
        TagJpaRepository tagJpaRepository,
        PostLikeJpaRepository postLikeJpaRepository,
        PostTagJpaRepository postTagJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.tagJpaRepository = tagJpaRepository;
        this.postLikeJpaRepository = postLikeJpaRepository;
        this.postTagJpaRepository = postTagJpaRepository;
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
    public int likePost(Long id, Long userId) {
        // ensure post exists (nice error message)
        if (!jpaRepository.existsById(id)) {
            throw new IllegalArgumentException("post not found: " + id);
        }

        int inserted = postLikeJpaRepository.insertIgnore(id, userId);
        if (inserted == 1) {
            jpaRepository.incrementLikes(id);
        }

        Integer count = jpaRepository.findLikesCount(id);
        return count == null ? 0 : count;
    }

    @Override
    public boolean hasLikedPost(Long id, Long userId) {
        return postLikeJpaRepository.existsByIdPostIdAndIdUserId(id, userId);
    }

    @Override
    public List<Post> findAll() {
        return jpaRepository.findAllWithTags().stream().map(PostRepositoryAdapter::toDomain).toList();
    }

    @Override
    public PostPage findPage(int page, int size) {
        var pageable = PageRequest.of(page, size);
        var pageResult = jpaRepository.findPage(pageable);
        var posts = pageResult.getContent();
        if (posts.isEmpty()) {
            return new PostPage(
                List.of(),
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages(),
                pageResult.hasNext(),
                pageResult.hasPrevious());
        }

        var postIds = posts.stream().map(PostJpaEntity::getId).toList();
        var pts = postTagJpaRepository.findByPostIdInWithTag(postIds);

        Map<Long, List<String>> tagsByPostId =
            pts.stream()
                .collect(
                    Collectors.groupingBy(
                        pt -> pt.getPost().getId(),
                        Collectors.mapping(pt -> pt.getTag().getName(), Collectors.toList())));

        var items =
            posts.stream()
                .map(p -> toDomain(p, tagsByPostId.getOrDefault(p.getId(), List.of())))
                .toList();

        return new PostPage(
            items,
            pageResult.getNumber(),
            pageResult.getSize(),
            pageResult.getTotalElements(),
            pageResult.getTotalPages(),
            pageResult.hasNext(),
            pageResult.hasPrevious());
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
            e.getPostTags().stream().map(pt -> pt.getTag().getName()).sorted().toList();
        return toDomain(e, tagNames);
    }

    private static Post toDomain(PostJpaEntity e, List<String> tagNames) {
        return new Post(
            e.getId(),
            e.getTitle(),
            e.getContent(),
            e.getAuthorId(),
            tagNames == null ? List.of() : tagNames.stream().distinct().sorted().toList(),
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
