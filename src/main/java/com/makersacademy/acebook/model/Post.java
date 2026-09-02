package com.makersacademy.acebook.model;

import jakarta.persistence.*;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "POSTS")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;

//    migration creates the column as image_url,
//    so being explicit means there won't be issues mapping over this later on:
    @Column(name = "image_url")
    private String imageUrl;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("createdAt ASC")
    private List<Comment> comments = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // tells java to use the likes table and to connect
    // posts and users together
    @ManyToMany
    @JoinTable(
            name = "likes",
            // column in the likes points to the post
            joinColumns = @JoinColumn(name = "post_id"),
            // column in the likes points to the user
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )

    // stores all users who have liked a post
    // dont know much about sets but had to use this as i didnt
    // want user to like the same post twice
    // the UNIQUE (user_id, post_id) also does this, so technically 2 players of protection
    private Set<User> likedBy = new HashSet<>();
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Post() {}

    public Post(String content) {
        this.content = content;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
