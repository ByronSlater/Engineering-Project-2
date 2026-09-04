INSERT INTO friend_status (description) VALUES ('pending') ON CONFLICT DO NOTHING;
INSERT INTO friend_status (description) VALUES ('accepted') ON CONFLICT DO NOTHING;
INSERT INTO friend_status (description) VALUES ('declined') ON CONFLICT DO NOTHING;
