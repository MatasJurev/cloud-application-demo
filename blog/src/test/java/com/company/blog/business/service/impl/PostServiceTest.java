package com.company.blog.business.service.impl;

import com.company.blog.business.mappers.PostMapStructMapper;
import com.company.blog.business.repository.PostRepository;
import com.company.blog.business.repository.model.PostDAO;
import com.company.blog.model.Post;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PostServiceTest {
    @Mock
    private PostRepository repository;
    @InjectMocks
    private PostServiceImpl service;
    @Mock
    private PostMapStructMapper mapper;

    private Post post;
    private PostDAO postDAO;
    private List<Post> postList;
    private List<PostDAO> postDAOList;

    @BeforeAll
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @BeforeEach
    public void init() {
        post = createPost(1L, "author", "content", "title");
        postDAO = createPostDAO(1L, "author", "content", "title");
        postList = createPostList(post);
        postDAOList = createPostDAOList(postDAO);
    }

    @Test
    void testFindAllPosts() {
        when(repository.findAll()).thenReturn(postDAOList);
        when(mapper.postDAOToPost(postDAO)).thenReturn(post);
        List<Post> posts = service.findAllPosts();
        assertEquals(3, posts.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    void testFindAllPostsInvalid() {
        when(repository.findAll()).thenReturn(Collections.emptyList());
        assertTrue(service.findAllPosts().isEmpty());
        verify(repository, times(1)).findAll();
    }

    @Test
    void testFindPostById() {
        when(repository.findById(anyLong())).thenReturn(Optional.of(postDAO));
        when(mapper.postDAOToPost(postDAO)).thenReturn(post);
        Optional<Post> returnedPost = service.findPostByID(post.getId());
        assertEquals(post.getId(), returnedPost.get().getId());
        assertEquals(post.getAuthor(), returnedPost.get().getAuthor());
        assertEquals(post.getContent(), returnedPost.get().getContent());
        assertEquals(post.getTitle(), returnedPost.get().getTitle());
        verify(repository, times(1)).findById(anyLong());
    }

    @Test
    void testFindPostByIdInvalid() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());
        assertFalse(service.findPostByID(anyLong()).isPresent());
        verify(repository, times(1)).findById(anyLong());
    }

    @Test
    void testSavePost() {
        when(repository.save(postDAO)).thenReturn(postDAO);
        when(mapper.postDAOToPost(postDAO)).thenReturn(post);
        when(mapper.postToPostDAO(post)).thenReturn(postDAO);
        Post savedPost = service.savePost(post);
        assertTrue(service.hasNoMatch(savedPost));
        assertEquals(post, savedPost);
        verify(repository, times(1)).save(postDAO);
    }

    @Test
    void testSavePostInvalid() {
        when(repository.save(postDAO)).thenThrow(new IllegalArgumentException());
        when(mapper.postToPostDAO(post)).thenReturn(postDAO);
        assertThrows(IllegalArgumentException.class, () -> service.savePost(post));
        verify(repository, times(1)).save(postDAO);
    }

    @Test
    void testSavePostInvalidID() {
        Post postToSave = createPost(null, "author", "content", "title");
        when(repository.findAll()).thenReturn(postDAOList);
        assertThrows(HttpClientErrorException.class, () -> service.savePost(postToSave));
        verify(repository, times(0)). save(postDAO);
    }

    @Test
    void testDeletePost() {
        service.deletePost(anyLong());
        verify(repository, times(1)).deleteById(anyLong());
    }

    @Test
    void testDeletePostInvalid() {
        doThrow(new IllegalArgumentException()).when(repository).deleteById(anyLong());
        assertThrows(IllegalArgumentException.class, () -> service.deletePost(anyLong()));
    }

    private List<PostDAO> createPostDAOList(PostDAO postDAO) {
        List<PostDAO> postDAOList = new ArrayList<>();
        postDAOList.add(postDAO);
        postDAOList.add(postDAO);
        postDAOList.add(postDAO);
        return postDAOList;
    }

    private List<Post> createPostList(Post post) {
        List<Post> postList = new ArrayList<>();
        postList.add(post);
        postList.add(post);
        postList.add(post);
        return postList;
    }

    private PostDAO createPostDAO(Long id, String author, String content, String title) {
        PostDAO postDAO = new PostDAO();
        postDAO.setId(id);
        postDAO.setAuthor(author);
        postDAO.setContent(content);
        postDAO.setTitle(title);
        return postDAO;
    }

    private Post createPost(Long id, String author, String content, String title) {
        Post post = new Post();
        post.setId(id);
        post.setAuthor(author);
        post.setContent(content);
        post.setTitle(title);
        return post;
    }
}
