
ALTER TABLE users DROP COLUMN profile_picture;
ALTER TABLE users ADD COLUMN image_id BIGINT;
ALTER TABLE users ADD CONSTRAINT fk_profile_picture
FOREIGN KEY (image_id)
REFERENCES images(id);
