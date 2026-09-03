package com.makersacademy.acebook.repository;

import com.makersacademy.acebook.model.Post;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PostRepository extends CrudRepository<Post, Long> {
    @EntityGraph(attributePaths = {"comments"}) // tells java to load the comments

    List<Post> findAllByOrderByCreatedAtDesc(); //retrieve by order newest

    List<Post> findAllByOrderByCreatedAtAsc(); // retrive by order oldest

    // These Spring method names mean:

    // findBy
    // │
    // ├── Content
    // │
    // ├── Containing
    // │
    // ├── IgnoreCase
    // │
    // └── OrderBy CreatedAt Descending/Ascending

    List<Post> findByContentContainingIgnoreCaseOrderByCreatedAtDesc(String search);

    List<Post> findByContentContainingIgnoreCaseOrderByCreatedAtAsc(String search);
}
