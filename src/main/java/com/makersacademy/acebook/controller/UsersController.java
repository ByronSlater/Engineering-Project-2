package com.makersacademy.acebook.controller;

import com.makersacademy.acebook.model.User;
import com.makersacademy.acebook.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Random;

@RestController
public class UsersController {
    final UserRepository userRepository;

    UsersController(UserRepository userRepository) {
        this.userRepository = userRepository;
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
}
