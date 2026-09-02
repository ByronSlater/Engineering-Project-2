package com.makersacademy.acebook.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString(exclude = {"user", "post", "likedBy"})
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Size(min = 10, max = 255)
    @Column(nullable = false)
    private String text;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // One comment can be liked by many users
    // One user can also like many comments
    @ManyToMany
    @JoinTable(
            name = "comment_likes",


            joinColumns = @JoinColumn(name = "comment_id"),


            inverseJoinColumns = @JoinColumn(name = "user_id")
    )


    private Set<User> likedBy = new HashSet<>();

    public Comment() {}

    public Comment(
        String text,
        User user,
        Post post
    ) {
        this.text = text;
        this.user = user;
        this.post = post;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
