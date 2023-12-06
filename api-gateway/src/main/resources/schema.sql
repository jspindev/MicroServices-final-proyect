CREATE TABLE IF NOT EXISTS user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    roles VARCHAR(255) NOT NULL
);

INSERT INTO user (username, password, roles)
VALUES ('admin', '$2b$12$bW6LVpBOLrqqp8qKrkcT.er6WgKkVjxHNCV/SqinYGeI4xrQU4Tlm', 'ROLE_ADMIN')
ON DUPLICATE KEY UPDATE username=username;

