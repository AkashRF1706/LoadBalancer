INSERT INTO servers (url, is_alive, weight) VALUES ('http://localhost:8081', true, 3)
ON CONFLICT (url) DO NOTHING;

INSERT INTO servers (url, is_alive, weight) VALUES ('http://localhost:8082', true, 1)
ON CONFLICT (url) DO NOTHING;
