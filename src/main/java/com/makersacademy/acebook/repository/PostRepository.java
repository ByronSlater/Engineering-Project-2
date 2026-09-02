package com.makersacademy.acebook.repository;

import com.makersacademy.acebook.model.Post;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PostRepository extends CrudRepository<Post, Long> {
    @EntityGraph(attributePaths = {"comments"}) // tells java to load the comments
    List<Post> findAllByOrderByIdDesc();
}
