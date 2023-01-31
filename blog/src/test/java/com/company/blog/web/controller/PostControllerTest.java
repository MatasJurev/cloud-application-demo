package com.company.blog.web.controller;

import com.company.blog.business.service.PostService;
import com.company.blog.model.Post;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
class PostControllerTest {
    private final String URL = "/post";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PostController controller;
    @MockBean
    private PostService service;

    @Test
    void testFindAllPosts() throws Exception {
        List<Post> postList = createPostList(createPost());

        when(service.findAllPosts()).thenReturn(postList);

        mockMvc.perform(MockMvcRequestBuilders.get(URL))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(4)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].author").value("author"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].content").value("content"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].title").value("title"))
                .andExpect(status().isOk());

        verify(service, times(1)).findAllPosts();
    }

    @Test
    void testFindAllPostsInvalid() throws Exception {
        List<Post> postList = new ArrayList<>();

        when(service.findAllPosts()).thenReturn(postList);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(URL)
                        .content(asJsonString(postList))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(service, times(1)).findAllPosts();
    }

    @Test
    void testFindPostById() throws Exception {
        Optional<Post> post = Optional.of(createPost());

        when(service.findPostByID(anyLong())).thenReturn(post);

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.author").value("author"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").value("content"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("title"))
                .andExpect(status().isOk());

        verify(service, times(1)).findPostByID(anyLong());
    }

    @Test
    void testFindPostByIdInvalid() throws Exception {
        Optional<Post> post = Optional.of(createPost());
        post.get().setId(null);

        when(service.findPostByID(null)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .get(URL + null)
                        .content(asJsonString(post))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(service, times(0)).findPostByID(null);
    }

    @Test
    void testSavePost() throws Exception {
        Post post = createPost();
        post.setId(null);

        when(service.savePost(post)).thenReturn(post);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(URL)
                        .content(asJsonString(post))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        verify(service, times(1)).savePost(post);
    }

    @Test
    void testSavePostInvalid() throws Exception {
        Post post = createPost();
        post.setAuthor("");

        when(service.savePost(post)).thenReturn(post);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(URL)
                        .content(asJsonString(post))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        verify(service, times(0)).savePost(post);
    }

    @Test
    void testUpdatePostById() throws Exception {
        Post post = createPost();

        when(service.findPostByID(post.getId())).thenReturn(Optional.of(post));

        mockMvc.perform(MockMvcRequestBuilders
                        .put(URL + "/1")
                        .content(asJsonString(post))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L))
                .andExpect(status().isCreated());

        verify(service, times(1)).savePost(post);
    }

    @Test
    void testUpdatePostByIdInvalid() throws Exception {
        Post post = createPost();
        post.setId(null);

        when(service.findPostByID(null)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .put(URL + "/")
                        .content(asJsonString(post))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        verify(service, times(0)).savePost(post);
    }

    @Test
    void testDeletePost() throws Exception {
        Optional<Post> post = Optional.of(createPost());

        when(service.findPostByID(anyLong())).thenReturn(post);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete(URL + "/1")
                        .content(asJsonString(post))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(service, times(1)).deletePost(anyLong());
    }

    @Test
    void testDeletePostInvalid() throws Exception {
        Optional<Post> post = Optional.of(createPost());
        post.get().setId(null);

        when(service.findPostByID(null)).thenReturn(post);

        ResultActions mvcResult = mockMvc.perform(MockMvcRequestBuilders
                        .delete(URL + null)
                        .content(asJsonString(post))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(service, times(0)).deletePost(anyLong());
    }

    private Post createPost() {
        Post post = new Post();
        post.setId(1L);
        post.setAuthor("author");
        post.setContent("content");
        post.setTitle("title");
        return post;
    }

    private List<Post> createPostList(Post post) {
        List<Post> postList = new ArrayList<>();
        postList.add(post);
        postList.add(post);
        postList.add(post);
        postList.add(post);
        return postList;
    }

    private static String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
