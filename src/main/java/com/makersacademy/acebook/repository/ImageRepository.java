package com.makersacademy.acebook.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.makersacademy.acebook.model.Image;

public interface ImageRepository extends CrudRepository<Image, Long> {
    Optional<Image> findByImageHash(String imageHash);
}
