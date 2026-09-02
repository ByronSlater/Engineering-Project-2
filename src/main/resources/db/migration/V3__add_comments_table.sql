DROP TABLE IF EXISTS comments;

CREATE TABLE comments (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  text VARCHAR(255),
  created_at TIMESTAMP,
  user_id BIGINT,
  post_id BIGINT,

  CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id),
  CONSTRAINT fk_post FOREIGN KEY (post_id) REFERENCES posts(id)
);
