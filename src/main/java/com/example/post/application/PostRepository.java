package com.example.post.application;

import com.example.post.domain.Post;
import java.util.List;
import java.util.Optional;

public interface PostRepository {
    
    Post createPost(String title, String content, Long authorId, List<String> tagNames);

    Post modifyPost(Long id, String title, String content, List<String> tagNames);

    boolean deletePost(Long id);

    int likePost(Long id, Long userId);

    boolean hasLikedPost(Long id, Long userId);

    List<Post> findAll();

    PostPage findPage(int page, int size);
    
    Optional<Post> findById(Long id);

    List<Post> findByTag(String tagName);
}
