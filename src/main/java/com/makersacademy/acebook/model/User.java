package com.makersacademy.acebook.model;

import jakarta.persistence.*;
import lombok.Data;

import static java.lang.Boolean.TRUE;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private boolean enabled;
    private String bio;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id")
    private Image profilePicture;

    public String getImageLink() {
        if (this.profilePicture == null) {
            return "/image/pfp_cat1.jpg";
        }
        return "/images/" + this.profilePicture.getImageHash() + ".jpg";
    }

    public User() {
        this.enabled = TRUE;
    }


    public String getNickName() {
        return this.username.split("@")[0];
    }

    public User(String username) {
        this.username = username;
        this.enabled = TRUE;
    }

    public User(String username, boolean enabled) {
        this.username = username;
        this.enabled = enabled;
    }
}
