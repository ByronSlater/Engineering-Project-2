package com.makersacademy.acebook.service;

import com.makersacademy.acebook.repository.UserRepository;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.makersacademy.acebook.model.Image;
import com.makersacademy.acebook.model.Post;
import com.makersacademy.acebook.model.User;
import com.makersacademy.acebook.repository.ImageRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ImageService {
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;

    public ImageService(ImageRepository ImageRepository, UserRepository userRepository) {
        this.imageRepository = ImageRepository;
        this.userRepository = userRepository;
    }

    public void setProfilePicture(User user, byte[] imageContent) {
        Image img = new Image();


        img.setImageData(imageContent);

        String hash = UUID.randomUUID().toString().replace("-", "");
        img.setImageHash(hash);
        img.setImageData(imageContent);

        imageRepository.save(img);

        user.setProfilePicture(img);
        userRepository.save(user);
    }

    public long addImageToPost(Post post, byte[] imageContent) {
        Image img = new Image();

        String hash = UUID.randomUUID().toString().replace("-", "");
        img.setImageHash(hash);
        img.setImageData(imageContent);

        img.setPost(post);
        imageRepository.save(img);

        return img.getId();
    }

    public long uploadUnlinkedImage(byte[] imageContent) {
        Image img = new Image();

        String hash = UUID.randomUUID().toString().replace("-", "");
        img.setImageHash(hash);
        img.setImageData(imageContent);

        imageRepository.save(img);

        return img.getId();
    }
}
