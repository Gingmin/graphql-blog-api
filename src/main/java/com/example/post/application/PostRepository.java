package com.example.post.application;

import com.example.post.domain.Post;
import java.util.List;
import java.util.Optional;

public interface PostRepository {
    
    Post createPost(String title, String content, Long authorId, List<String> tagNames);

    Post modifyPost(Long id, String title, String content, List<String> tagNames);

    boolean deletePost(Long id);

    List<Post> findAll();
    
    Optional<Post> findById(Long id);

    List<Post> findByTag(String tagName);
}
