package com.makersacademy.acebook.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.makersacademy.acebook.model.Image;
import com.makersacademy.acebook.model.User;
import com.makersacademy.acebook.repository.ImageRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ImageService {
    private final ImageRepository imageRepository;

    public ImageService(ImageRepository ImageRepository) {
        this.imageRepository = ImageRepository;
    }

    public void setProfilePicture(User user, byte[] imageContent) {
        Image img = new Image();


        img.setImageData(imageContent);

        String hash = UUID.randomUUID().toString().replace("-", "");
        img.setImageHash(hash);
        img.setImageData(imageContent);

        imageRepository.save(img);
    }

    public void uploadUnlinkedImage(byte[] imageContent) {
        Image img = new Image();

        String hash = UUID.randomUUID().toString().replace("-", "");
        img.setImageHash(hash);
        img.setImageData(imageContent);

        imageRepository.save(img);
    }
}
