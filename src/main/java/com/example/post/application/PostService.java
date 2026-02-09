package com.example.post.application;

import com.example.post.domain.Post;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostService {
    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Transactional(readOnly = true)
    public PostPage posts() {
        return posts(0, 20);
    }

    @Transactional(readOnly = true)
    public PostPage posts(Integer page, Integer size) {
        int p = page == null ? 0 : page;
        int s = size == null ? 20 : size;
        if (p < 0) {
            throw new IllegalArgumentException("page must be >= 0");
        }
        if (s < 1 || s > 100) {
            throw new IllegalArgumentException("size must be between 1 and 100");
        }
        return postRepository.findPage(p, s);
    }

    @Transactional(readOnly = true)
    public Post post(Long id) {
        return postRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("post not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<Post> postsByTag(String tagName) {
        return postRepository.findByTag(tagName);
    }

    @Transactional
    public Post createPost(String title, String content, Long authorId, List<String> tagNames) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title must not be blank");
        }
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("content must not be blank");
        }
        if (authorId == null) {
            throw new IllegalArgumentException("authorId must not be null");
        }
        return postRepository.createPost(title, content, authorId, tagNames);
    }

    @Transactional
    public Post modifyPost(Long id, String title, String content, List<String> tagNames) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title must not be blank");
        }
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("content must not be blank");
        }
        if (id == null) {
            throw new IllegalArgumentException("id must not be null");
        }
        return postRepository.modifyPost(id, title, content, tagNames);
    }

    @Transactional
    public boolean deletePost(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id must not be null");
        }
        return postRepository.deletePost(id);
    }

    @Transactional
    public int likePost(Long id, Long userId) {
        if (id == null) {
            throw new IllegalArgumentException("id must not be null");
        }
        if (userId == null) {
            throw new IllegalArgumentException("userId must not be null");
        }
        return postRepository.likePost(id, userId);
    }

    @Transactional(readOnly = true)
    public boolean hasLikedPost(Long id, Long userId) {
        if (id == null || userId == null) return false;
        return postRepository.hasLikedPost(id, userId);
    }
}
