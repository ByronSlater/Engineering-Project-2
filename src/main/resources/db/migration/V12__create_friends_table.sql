CREATE TABLE friend_status (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    description VARCHAR(10) UNIQUE
);

CREATE TABLE friends (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  sender_id BIGINT,
  receiver_id BIGINT,
  status_id BIGINT,

  CONSTRAINT fk_sender FOREIGN KEY (sender_id) REFERENCES users(id),
  CONSTRAINT fk_receiver FOREIGN KEY (receiver_id) REFERENCES users(id),

  CONSTRAINT different_users CHECK (sender_id <> receiver_id)
);
