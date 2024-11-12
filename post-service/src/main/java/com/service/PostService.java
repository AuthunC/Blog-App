package com.service;

import com.dto.UserDTO;
import com.feign.UserClient;
import com.model.Post;
import com.repository.PostRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostService {

	private final PostRepository postRepository;
	private final UserClient userClient;

	public PostService(PostRepository postRepository, UserClient userClient) {
		this.postRepository = postRepository;
		this.userClient = userClient;
	}

	public List<Post> getAllPosts() {
		List<Post> posts = postRepository.findAll();
		return posts.stream().map(this::populateAuthorDetails).collect(Collectors.toList());
	}

	public Optional<Post> getPostById(Long id) {
		return postRepository.findById(id).map(this::populateAuthorDetails);
	}

	private Post populateAuthorDetails(Post post) {
		try {
			if (post.getAuthorId() != null) {
				UserDTO author = userClient.getUserById(post.getAuthorId()); // Use authorId here
				post.setAuthor(author);
			}
		} catch (Exception e) {
			System.err.println("Failed to retrieve author details for post ID: " + post.getId());
			post.setAuthor(null);
		}
		return post;
	}

	public Post createPost(Post post) {
		post.setCreatedAt(LocalDateTime.now());
		post.setUpdatedAt(LocalDateTime.now());
		return postRepository.save(post);
	}

	public Post updatePost(Long id, Post postDetails) {
		Post post = postRepository.findById(id).orElseThrow(() -> new RuntimeException("Post not found with id " + id));
	    
		post.setTitle(postDetails.getTitle());
		post.setContent(postDetails.getContent());
		
		if (postDetails.getAuthor() != null) {
	        UserDTO author = userClient.getUserById(postDetails.getAuthor().getId());
	        post.setAuthor(author);
	    }
		
		post.setUpdatedAt(LocalDateTime.now());

		return postRepository.save(post);
	}

	public void deletePost(Long id) {
		Post post = postRepository.findById(id).orElseThrow(() -> new RuntimeException("Post not found with id " + id));
		postRepository.delete(post);
	}
	
	public List<Post> searchPosts(String keyword) {
		if(keyword.length()==0)
			return null;
        List<Post> searchPosts = postRepository.searchPostsByKeyword(keyword);
        
        searchPosts.forEach(post -> {
            UserDTO author = userClient.getUserById(post.getAuthorId());
            post.setAuthor(author); // Ensure the Post model has a setter for Author
        });
        
        return searchPosts;
    }

}
