package com.makersacademy.acebook.controller;

import com.makersacademy.acebook.model.Image;
import com.makersacademy.acebook.repository.ImageRepository;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import com.makersacademy.acebook.service.ImageService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ImageController {
    private final ImageService imageService;
    private final ImageRepository imageRepository;

    public ImageController(
        ImageService imageService,
        ImageRepository imageRepository
    ) {
        this.imageService = imageService;
        this.imageRepository = imageRepository;
    }

    @GetMapping("/images/upload")
    public String uploadPage(Model model) {
        model.addAttribute("images", imageRepository.findAll());

        return "images";
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
