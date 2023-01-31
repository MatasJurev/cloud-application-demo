package com.company.blog.business.repository;

import com.company.blog.business.repository.model.PostDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<PostDAO, Long> {
}
