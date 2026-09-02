package com.makersacademy.acebook.controller;

import com.makersacademy.acebook.model.Post;
import com.makersacademy.acebook.model.User;
import com.makersacademy.acebook.repository.PostRepository;

import com.makersacademy.acebook.repository.UserRepository;
import org.springframework.lang.NonNull;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class PostsController {
    final PostRepository repository;
    final UserRepository userRepository;

    PostsController(PostRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }


    @GetMapping("/posts")
    public String index(Model model) {
        Iterable<Post> posts = repository.findAllByOrderByIdDesc();
        model.addAttribute("posts", posts);
        model.addAttribute("post", new Post());
        return "posts/index";
    }

    @PostMapping("/posts")
    public RedirectView create(@ModelAttribute @NonNull Post post, @AuthenticationPrincipal DefaultOidcUser principal) {

        String username = (String) principal.getAttributes().get("email");
        User user = userRepository.findUserByUsername(username).orElseThrow();
        post.setUser(user);
        repository.save(post);
        return new RedirectView("/posts");
    }

    @PostMapping("/posts/{id}/like")
    public RedirectView like(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal OidcUser principal) {

        // find post that user clicked like on
        Post post = repository.findById(id).orElseThrow();

        // get email address of person logged in
        String username = principal.getEmail();

        // find the logge din person in the users table
        User user = userRepository.findUserByUsername(username).orElseThrow();

        // add the user to the list of people who liked the post
        post.getLikedBy().add(user);

        // save the updated post
        repository.save(post);

        // send user back to posts page
        return new RedirectView("/posts");
    }

}
