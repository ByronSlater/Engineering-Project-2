package com.makersacademy.acebook.controller;

import com.makersacademy.acebook.repository.UserRepository;
import com.makersacademy.acebook.service.FriendService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.view.RedirectView;


@Controller
public class FriendController {

    private final UserRepository userRepository;
    private final FriendService friendService;

    FriendController(FriendService friendService, UserRepository userRepository) {
        this.friendService = friendService;
        this.userRepository = userRepository;
    }

    @PostMapping("/makefriend/{id}")
    public RedirectView sendFriendshipRequest(
        @PathVariable("id") long receiverId,
        @AuthenticationPrincipal OidcUser loggedInUser
    ) {
        var sender = userRepository.findUserByUsername(loggedInUser.getEmail()).get();
        var receiver = userRepository.findById(receiverId).get();

        friendService.sendFriendshipRequest(sender, receiver);

        return new RedirectView("/profile/" + receiverId);
    }

    @PostMapping("/acceptfriend/{id}")
    public RedirectView acceptFriendshipRequest(
        @PathVariable("id") long senderId,
        @AuthenticationPrincipal OidcUser loggedInUser
    ) {
        var receiver = userRepository.findUserByUsername(loggedInUser.getEmail()).get();
        var sender = userRepository.findById(senderId).get();

        friendService.acceptFriendshipRequest(sender, receiver);

        return new RedirectView("/profile/" + senderId);
    }

    @PostMapping("/declinefriend/{id}")
    public RedirectView declineFriendshipRequest(
        @PathVariable("id") long senderId,
        @AuthenticationPrincipal OidcUser loggedInUser
    ) {
        var receiver = userRepository.findUserByUsername(loggedInUser.getEmail()).get();
        var sender = userRepository.findById(senderId).get();

        friendService.declineFriendshipRequest(sender, receiver);

        return new RedirectView("/profile/" + senderId);
    }
}
