package com.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.model.Post;

import feign.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

	@Query("SELECT p FROM Post p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) "
			+ "OR LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%'))")
	List<Post> searchPostsByKeyword(@Param("keyword") String keyword);
}
