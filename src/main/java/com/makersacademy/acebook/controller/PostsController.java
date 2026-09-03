package com.makersacademy.acebook.controller;

import com.makersacademy.acebook.form.CommentForm;
import com.makersacademy.acebook.model.Post;
import com.makersacademy.acebook.model.User;
import com.makersacademy.acebook.repository.PostRepository;
import com.makersacademy.acebook.repository.UserRepository;

import com.makersacademy.acebook.service.CommentService;

import com.makersacademy.acebook.service.ImageService;
import com.makersacademy.acebook.service.PostService;
import jakarta.validation.Valid;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class PostsController {
    private final ImageService imageService;
    private final PostRepository postRepository;
    private final PostService postService;
    private final CommentService commentService;
    private final UserRepository userRepository;

    PostsController(UserRepository userRepository, CommentService commentService, PostService postService, PostRepository postRepository, ImageService imageService) {
        this.postService = postService;
        this.postRepository = postRepository;
        this.commentService = commentService;
        this.userRepository = userRepository;
        this.imageService = imageService;
    }


    @GetMapping("/posts")
    public String index(
            @RequestParam(name = "sort", defaultValue = "newest") String sort,
            @RequestParam(name = "search", defaultValue = "") String search, // second request parameter for searching through posts, the default value means that simply visiting /posts should still work
            @AuthenticationPrincipal OidcUser oidcUser,
            Model model) {

        var posts = postService.allPosts(sort, search);

        model.addAttribute("posts", posts);
        model.addAttribute("post", new Post());
        model.addAttribute("sort", sort);
        model.addAttribute("search", search);

        model.addAttribute("commentForm", new CommentForm());

        model.addAttribute("loggedInUsername", oidcUser.getEmail());
        return "posts/index";
    }

    @PostMapping("/posts")
    public RedirectView create(
        @ModelAttribute @NonNull Post post,
        @AuthenticationPrincipal DefaultOidcUser principal,
        @RequestParam("file") MultipartFile file
) {

        String username = (String) principal.getAttributes().get("email");
        User user = userRepository.findUserByUsername(username).orElseThrow();
        post.setUser(user);
        postRepository.save(post);

        if (file != null) {
            try {
                imageService.addImageToPost(post, file.getBytes());
            } catch (IOException e) {}
        }

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

    @PostMapping("/posts/{postId}/delete")
    public String deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal OidcUser oidcUser
    ) {
        String email = oidcUser.getEmail();

        Long loggedInUserId = userRepository.findUserByUsername(email)
                .orElseThrow(() -> new IllegalStateException("Logged-in user is not registered"))
                .getId();

        postService.deletePost(postId, loggedInUserId);

        return "redirect:/posts";
    }

}
