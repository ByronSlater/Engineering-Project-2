package com.makersacademy.acebook.controller;

import com.makersacademy.acebook.form.CommentForm;
import com.makersacademy.acebook.model.Post;
import com.makersacademy.acebook.model.User;
import com.makersacademy.acebook.repository.PostRepository;
import com.makersacademy.acebook.repository.UserRepository;

import com.makersacademy.acebook.service.CommentService;

import com.makersacademy.acebook.service.PostService;
import jakarta.validation.Valid;

import org.springframework.lang.NonNull;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class PostsController {
    private final PostRepository postRepository;
    private final PostService postService;
    private final CommentService commentService;
    private final UserRepository userRepository;

    PostsController(UserRepository userRepository, CommentService commentService, PostService postService, PostRepository postRepository) {
        this.postService = postService;
        this.postRepository = postRepository;
        this.commentService = commentService;
        this.userRepository = userRepository;
    }


    @GetMapping("/posts")
    public String index(Model model) {
        var posts = postService.allPosts();

        model.addAttribute("posts", posts);
        model.addAttribute("post", new Post());

        model.addAttribute("commentForm", new CommentForm());
        return "posts/index";
    }

    @PostMapping("/posts")
    public RedirectView create(@ModelAttribute @NonNull Post post, @AuthenticationPrincipal DefaultOidcUser principal) {

        String username = (String) principal.getAttributes().get("email");
        User user = userRepository.findUserByUsername(username).orElseThrow();
        post.setUser(user);
        postRepository.save(post);
        return new RedirectView("/posts");
    }

    @PostMapping("/posts/{id}/like")
    public RedirectView like(
            @PathVariable("id") @NonNull Long id,
            @AuthenticationPrincipal OidcUser principal) {

        // find post that user clicked like on
        Post post = postRepository.findById(id).orElseThrow();

        // get email address of person logged in
        String username = principal.getEmail();

        // find the logge din person in the users table
        User user = userRepository.findUserByUsername(username).orElseThrow();

        // add the user to the list of people who liked the post
        post.getLikedBy().add(user);

        // save the updated post
        postRepository.save(post);

        // send user back to posts page
        return new RedirectView("/posts");
    }

    @PostMapping("/posts/{postId}/comments")
    public String addComment(
        @PathVariable long postId,
        @Valid @ModelAttribute CommentForm commentForm,
        BindingResult bindingResult,
        RedirectAttributes redirectAttributes,
        @AuthenticationPrincipal OidcUser oidcUser
    ) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.commentForm", bindingResult);
            redirectAttributes.addFlashAttribute("commentForm", commentForm);
            return "redirect:/posts";
        }

        String email = oidcUser.getEmail();
        long loggedInUserId = userRepository.findUserByUsername(email)
            .orElseThrow(() -> new IllegalStateException("Logged-in user is not registered"))
            .getId();
        commentService.addCommentToPost(postId, loggedInUserId, commentForm.getText());

        return "redirect:/posts";
    }

    @PostMapping("/comments/{commentId}/like")
    public String likeComment(
            @PathVariable long commentId,
            @AuthenticationPrincipal OidcUser oidcUser
    ) {
        String email = oidcUser.getEmail();

        long loggedInUserId = userRepository.findUserByUsername(email)
                .orElseThrow(() -> new IllegalStateException("Logged-in user is not registered"))
                .getId();

        commentService.likeComment(commentId, loggedInUserId);

        return "redirect:/posts";
    }
}
