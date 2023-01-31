package com.company.blog.business.service.impl;

import com.company.blog.business.mappers.PostMapStructMapper;
import com.company.blog.business.repository.PostRepository;
import com.company.blog.business.repository.model.PostDAO;
import com.company.blog.business.service.PostService;
import com.company.blog.model.Post;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
public class PostServiceImpl implements PostService {
    @Autowired
    PostRepository postRepository;
    @Autowired
    PostMapStructMapper postMapStructMapper;

    @Override
    public Optional<Post> findPostByID(Long id) {
        Optional<Post> postByID = postRepository.findById(id)
                .flatMap(product -> Optional.ofNullable(postMapStructMapper.postDAOToPost(product)));
        log.info("Post with id {} is {}", id, postByID);
        return postByID;
    }

    @Override
    public List<Post> findAllPosts() {
        List<PostDAO> postDAOList = postRepository.findAll();
        log.info("Get post list. Size is: {}", postDAOList::size);
        return postDAOList.stream().map(postMapStructMapper::postDAOToPost).collect(Collectors.toList());
    }

    @Override
    public Post savePost(Post post) {
        if(!hasNoMatch(post)) {
            log.error("Post conflict exception is thrown: {}", HttpStatus.CONFLICT);
            throw new HttpClientErrorException(HttpStatus.CONFLICT);
        }
        PostDAO postSaved = postRepository.save(postMapStructMapper.postToPostDAO(post));
        log.info("New post saved: {}", () -> postSaved);
        return postMapStructMapper.postDAOToPost(postSaved);
    }

    @Override
    public void deletePost(Long id) {
        postRepository.deleteById(id);
        log.info("Post with id {} was deleted", id);
    }

    public boolean hasNoMatch(Post post) {
        return postRepository.findAll().stream()
                .noneMatch(t -> !t.getId().equals(post.getId()) &&
                        t.getAuthor().equals(post.getAuthor()) &&
                        t.getContent().equals(post.getContent()) &&
                        t.getTitle().equals(post.getTitle()));
    }
}
