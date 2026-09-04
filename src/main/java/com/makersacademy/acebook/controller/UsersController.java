package com.makersacademy.acebook.controller;


import com.makersacademy.acebook.model.User;
import com.makersacademy.acebook.repository.PostRepository;
import com.makersacademy.acebook.repository.UserRepository;
import com.makersacademy.acebook.service.FriendService;
import com.makersacademy.acebook.service.PostService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;


import java.util.Random;
import java.util.stream.Collectors;

@RestController
public class UsersController {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostService postService;
    private final FriendService friendService;

    UsersController(
        UserRepository userRepository,
        PostRepository postRepository,
        PostService postService,
        FriendService friendService
    ) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.postService = postService;
        this.friendService = friendService;
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
    public ModelAndView DisplayProfile (
        @PathVariable Long id,
        @RequestParam(defaultValue = "newest") String sort,
        @AuthenticationPrincipal DefaultOidcUser principal
    ){
        User user = userRepository.findById(id).orElseThrow();
        var posts = postService.allPosts(sort, "").stream().filter(post -> post.getUser().getId().equals(user.getId())).toList();

        ModelAndView Profile = new ModelAndView("profile");
        var likedPostIds = posts.stream()
            .filter(post -> post.getLikedBy().stream()
                .anyMatch(liker -> user.getUsername().equals(liker.getUsername())))
            .map(post -> post.getId())
            .collect(Collectors.toSet());

        var loggedInUser = userRepository.findUserByUsername(principal.getEmail()).get();

        String friendRequestStatus;

        if (loggedInUser == user) {
            friendRequestStatus = "none";
        } else {
            friendRequestStatus = friendService.getFriendshipStatus(loggedInUser, user);
        }

        Profile.addObject("friendStatus", friendRequestStatus);
        Profile.addObject("user", user);
        Profile.addObject("posts", posts);
        Profile.addObject("likedPostIds", likedPostIds);
        return Profile;
    }

    @GetMapping("/profile/{id}/edit")
    public ModelAndView EditBioForm(@PathVariable Long id){
        User user = userRepository.findById(id).orElseThrow();
        ModelAndView model = new ModelAndView("editbio");
        model.addObject("user", user);
        return model;
    }

    @PostMapping("/profile/{id}/bio")
    public RedirectView updateBio(@PathVariable Long id, @ModelAttribute User formuser){
        User user = userRepository.findById(id).orElseThrow();
        user.setBio(formuser.getBio());
        userRepository.save(user);
        return new RedirectView("/profile/" + id);
    }



}
