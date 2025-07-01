INSERT INTO servers (url, is_alive) 
VALUES ('http://localhost:8081', true)
ON CONFLICT (url) DO NOTHING;

INSERT INTO servers (url, is_alive) 
VALUES ('http://localhost:8082', true)
ON CONFLICT (url) DO NOTHING;
