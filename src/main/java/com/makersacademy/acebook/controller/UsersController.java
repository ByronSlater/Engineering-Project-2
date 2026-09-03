package com.makersacademy.acebook.controller;


import com.makersacademy.acebook.model.Post;
import com.makersacademy.acebook.model.User;
import com.makersacademy.acebook.repository.PostRepository;
import com.makersacademy.acebook.repository.UserRepository;
import com.makersacademy.acebook.service.PostService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;


import java.util.Random;

@RestController
public class UsersController {
    final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostService postService;

    UsersController(UserRepository userRepository, PostRepository postRepository, PostService postService) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.postService = postService;
    }

    @GetMapping("/users/after-login")
    public RedirectView afterLogin() {
        DefaultOidcUser principal = (DefaultOidcUser) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        String username = (String) principal.getAttributes().get("email");
        userRepository
                .findUserByUsername(username)
                .orElseGet(() -> {
                    User user = new User(username);
                    user.setProfile_picture(GetRandompp());
                    return userRepository.save(user);
                });

        return new RedirectView("/posts");
    }

    // method to randomly select a profile picture
    // not sure the controller is the best place to put it but from doing a lil research
    // it seemed like the least controversial pick
    private String GetRandompp(){
        String[] profiles = {
                "profile1.jpeg",
                "profile2.jpeg",
                "profile3.jpeg",
                "profile4.jpeg",
                "profile5.jpg"
        };
        Random random = new Random();
        return profiles[random.nextInt(profiles.length)];
    }

    @GetMapping("/profile")
    public RedirectView profiles(@AuthenticationPrincipal DefaultOidcUser principal){
        String username = principal.getEmail();

        User user = userRepository.findUserByUsername(username).orElseThrow();
        RedirectView myredirectView = new RedirectView("/profile/" + user.getId());
        return myredirectView;
    }

    @GetMapping("/profile/{id}")
    public ModelAndView DisplayProfile (@PathVariable Long id){
        User user = userRepository.findById(id).orElseThrow();
        var posts = postService.allPosts().stream().filter(post -> post.getUser().getId().equals(user.getId())).toList();

        ModelAndView Profile = new ModelAndView("profile");
        Profile.addObject("user", user);
        Profile.addObject("posts", posts);
        return Profile;
    }


}
