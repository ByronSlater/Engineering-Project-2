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

    // editing posts - this enforces ownership over posts when the user opens the Edit page
    // rather than duplicating it into the controller I am putting it here and simplifying my initial code, 
    // which was similar to the above deletePost() method written by Michael.
    // both opening the edit form and submitting an edit will now enforce 
    // the same ownership rule

    public Post getPostForEditing(Long postId, Long loggedInUserId) {

    Post post = postRepository.findById(postId).orElseThrow();

    if (!post.getUser().getId().equals(loggedInUserId)) {
        throw new IllegalStateException("You can only edit your own posts");
    }

    return post;
}


    public void editPost(Long postId, Long loggedInUserId, String newContent) {

        Post post = getPostForEditing(postId, loggedInUserId);

        // This deals with server side blank protection as people can technically bypass HTML validation
        // with the required statmement

        if (newContent == null || newContent.isBlank()) {
            throw new IllegalStateException("Your post content cannot be left empty!");
        }

        String updatedContent = newContent.trim(); // Get rid of any front trailing whitespace

        if (updatedContent.length() > 250) { // if the post is over 250 characters long
            throw new IllegalArgumentException("Your post cannot exceed 250 characters.");
        }

        post.setContent(updatedContent);

        postRepository.save(post);
    }

}
