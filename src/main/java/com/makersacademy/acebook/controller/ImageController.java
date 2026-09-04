package com.makersacademy.acebook.controller;

import com.makersacademy.acebook.model.Image;
import com.makersacademy.acebook.repository.ImageRepository;

import com.makersacademy.acebook.repository.UserRepository;
import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import com.makersacademy.acebook.service.ImageService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ImageController {
    private final UserRepository userRepository;
    private final ImageService imageService;
    private final ImageRepository imageRepository;

    public ImageController(
        ImageService imageService,
        ImageRepository imageRepository,
        UserRepository userRepository
    ) {
        this.userRepository = userRepository;
        this.imageService = imageService;
        this.imageRepository = imageRepository;
    }

    @GetMapping("/images/upload")
    public String uploadPage(Model model) {
        model.addAttribute("images", imageRepository.findAll());

        return "images";
    }

    @PostMapping("/upload_pp/{id}")
    public RedirectView uploadPP(
        @PathVariable("id") long id,
        @RequestParam("file") MultipartFile file,
        @AuthenticationPrincipal DefaultOidcUser principal
    ) {

        var user = userRepository.findUserByUsername(principal.getEmail()).get();

        // prevent users from uploading pps for other people
        if (user.getId() != id) {
            return new RedirectView("/profile/" + id);
        }

        try {
            imageService.setProfilePicture(user, file.getBytes());
        } catch(IOException e) {}

        return new RedirectView("/profile/" + id);
    }

    @GetMapping("/images/{hash}.jpg")
    public ResponseEntity<byte[]> getImage (
            @PathVariable("hash") @NonNull String hash) {


        Image img = imageRepository.findByImageHash(hash).get();
        MediaType mediaType = MediaType.parseMediaType("image/jpeg");

        return ResponseEntity
                .ok()
                .contentType(mediaType)
                .body(img.getImageData());
    }

    @PostMapping("/images/upload")
    public RedirectView uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            imageService.uploadUnlinkedImage(file.getBytes());

        } catch(IOException e) {
        }
        return new RedirectView("/images/upload");
    }

}
