CREATE TABLE images (
    id BIGSERIAL PRIMARY KEY,
    post_id BIGINT,
    image_hash VARCHAR(64) NOT NULL UNIQUE, -- will identify images through url using this
    image_data BYTEA NOT NULL,

    FOREIGN KEY (post_id) REFERENCES posts(id)
);

-- indexing it means lookups against it are really fast
CREATE INDEX idx_images_hash ON images(image_hash);
