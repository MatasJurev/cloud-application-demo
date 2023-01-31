package com.company.blog.business.service;

import com.company.blog.model.Post;

import java.util.List;
import java.util.Optional;

public interface PostService {
    Optional<Post> findPostByID(Long id);

    List<Post> findAllPosts();

    Post savePost(Post post);

    void deletePost(Long id);
}
