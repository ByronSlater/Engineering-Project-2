CREATE TABLE comment_likes (
    user_id bigint NOT NULL,
    comment_id bigint NOT NULL,

    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (comment_id) REFERENCES comments(id),

    UNIQUE (user_id, comment_id)
);