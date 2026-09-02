package com.makersacademy.acebook.repository;

import com.makersacademy.acebook.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(
    replace = AutoConfigureTestDatabase.Replace.NONE
)
public class PostRepositoryTest {

@Autowired
private PostRepository repository;

@BeforeEach
public void setup() {
    repository.deleteAll();
}

@Test
public void ordersbyNewestPost() {
    Post olderPost = repository.save(new Post("Older Post"));
    Post newerPost = repository.save(new Post("Newer Post"));

    // This is a neat trick used by Spring Data. No SQL queries need to be passed through. instead, it examines the method name and derives a query resembling
    // SELECT * FROM posts ORDER BY id DESC;
    // like this:

    // findAllByOrderByIdDesc
    // │       │       │ │
    // │       │       │ └── descending
    // │       │       └──── id
    // │       └──────────── order by
    // └──────────────────── find all

    // which means, find all my posts by id, in descending order.

    List<Post> posts = repository.findAllByOrderByCreatedAtDesc();

    assertEquals(newerPost.getId(), posts.get(0).getId());
    assertEquals(olderPost.getId(), posts.get(1).getId());

    }

// The same trick will be used here for sorting by oldest.

@Test
public void ordersByOldestPost() {
    Post olderPost = repository.save(new Post("Older Post"));
    Post newerPost = repository.save(new Post("Newer Post"));

    List<Post> posts = repository.findAllByOrderByCreatedAtAsc();

    assertEquals(olderPost.getId(), posts.get(0).getId());
    assertEquals(newerPost.getId(), posts.get(1).getId());
}
}
