package com.company.blog.web.controller;

import com.company.blog.business.service.PostService;
import com.company.blog.model.Post;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Log4j2
@RestController
@RequestMapping("/post")
public class PostController {
    @Autowired
    PostService postService;

    @GetMapping
    public ResponseEntity<List<Post>> findAllPosts() {
        log.info("Retrieving list of posts");
        List<Post> postList = postService.findAllPosts();

        if(postList.isEmpty()) {
            log.warn("Posts list is empty: {}", postList);
            return ResponseEntity.notFound().build();
        }
        log.info("Posts list is found. Size: {}", postList::size);

        return ResponseEntity.ok(postList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> findPostByID(@NonNull @PathVariable Long id) {
        log.info("Finding post by passing post id {}", id);
        Optional<Post> post = (postService.findPostByID(id));

        if(post.isEmpty()) {
            log.warn("Post with id {} is not found", id);
        } else {
            log.info("Post with id {} is found: {}", id, post);
        }

        return post.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Post> savePost(@Valid @RequestBody Post post, BindingResult bindingResult) {
        log.info("Create new post by passing {}", post);
        if(bindingResult.hasErrors()) {
            log.error("New post is not created: {}", bindingResult);
            return ResponseEntity.badRequest().build();
        }

        Post postSaved = postService.savePost(post);
        log.info("New post is created: {}", post);
        return new ResponseEntity<>(postSaved, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Post> deletePostByID(@NonNull @PathVariable Long id) {
        log.info("Delete post by passing id {}", id);
        Optional<Post> post = postService.findPostByID(id);

        if(post.isEmpty()) {
            log.warn("Post with id {} not found", id);
            return ResponseEntity.notFound().build();
        }

        postService.deletePost(id);
        log.info("Post with id {} is deleted: {}", id, post);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<Post> updatePostByID(@NonNull @PathVariable Long id,
                                                  @Valid @RequestBody Post post, BindingResult bindingResult) {
        post.setId(id);
        log.info("Update existing post with id: {} and new body: {}", id, post);

        if (bindingResult.hasErrors() || !id.equals(post.getId())) {
            log.warn("Post with id {} not found", id);
            return ResponseEntity.notFound().build();
        }

        postService.savePost(post);
        log.info("Post with id {} is updated: {}", id, post);
        return new ResponseEntity<>(post, HttpStatus.CREATED);
    }
}
