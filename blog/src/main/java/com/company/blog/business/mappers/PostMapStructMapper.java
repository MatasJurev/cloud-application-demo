package com.company.blog.business.mappers;

import com.company.blog.business.repository.model.PostDAO;
import com.company.blog.model.Post;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PostMapStructMapper {
    PostDAO postToPostDAO(Post post);
    Post postDAOToPost(PostDAO postDAO);
}
