package com.makersacademy.acebook.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
public class FriendController {
    @PostMapping("/makefriend/{id}")
    public String sendFriendshipRequest(
        @PathVariable("id") long receiverId,
        @AuthenticationPrincipal OidcUser sender
    ) {


        return "";
    }
}
