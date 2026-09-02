package com.makersacademy.acebook.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.makersacademy.acebook.model.Post;
import com.makersacademy.acebook.repository.CommentRepository;
import com.makersacademy.acebook.repository.PostRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    // Constructor injection
    public PostService(PostRepository postRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    public List<Post> allPosts(String sort) {
        if ("oldest".equals(sort)) {
            return postRepository.findAllByOrderByCreatedAtAsc();
        }

        return postRepository.findAllByOrderByCreatedAtDesc();
    }
}
