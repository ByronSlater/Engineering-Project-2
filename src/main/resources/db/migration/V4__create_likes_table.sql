CREATE TABLE likes(
    user_id bigint NOT NULL,
    post_id bigint NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (post_id) REFERENCES posts(id),
    UNIQUE (user_id, post_id));