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

    // Dealing with sort and search

    // the new logic here is as follows:

    // Is there a search?
    // │
    // ├── YES
    // │    │
    // │    ├── oldest? → search + ascending
    // │    │
    // │    └── newest? → search + descending
    // │
    // └── NO
    //     │
    //     ├── oldest? → all + ascending
    //     │
    //     └── newest? → all + descending

    public List<Post> allPosts(String sort, String search) {

        if (search != null && !search.isBlank()) {

            if ("oldest".equals(sort)) {
                return postRepository.findByContentContainingIgnoreCaseOrderByCreatedAtAsc(search.trim()); // search.trim() deals with accidental whitespace before or after a search
            }

            return postRepository.findByContentContainingIgnoreCaseOrderByCreatedAtDesc(search.trim());
        }

        if ("oldest".equals(sort)) {
            return postRepository.findAllByOrderByCreatedAtAsc();
        }

        return postRepository.findAllByOrderByCreatedAtDesc();

    }

    public void deletePost(Long postId, Long loggedInUserId) {
        Post post = postRepository.findById(postId).orElseThrow();

        if (!post.getUser().getId().equals(loggedInUserId)) {
            throw new IllegalStateException("You can only delete your own posts");
        }

        postRepository.delete(post);
    }
}
