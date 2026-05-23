-- Music Island Database Schema

CREATE DATABASE IF NOT EXISTS music_island DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE music_island;

-- 1. User table
CREATE TABLE IF NOT EXISTS user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(100),
    avatar VARCHAR(255),
    role VARCHAR(20) DEFAULT 'USER',
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Song table
CREATE TABLE IF NOT EXISTS song (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    artist VARCHAR(200),
    album VARCHAR(200),
    genre VARCHAR(50),
    duration INT,
    cover_url VARCHAR(500),
    file_url VARCHAR(500),
    play_count BIGINT DEFAULT 0,
    like_count BIGINT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Playlist table
CREATE TABLE IF NOT EXISTS playlist (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    cover_url VARCHAR(500),
    user_id BIGINT NOT NULL,
    is_public TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Playlist-Song relation
CREATE TABLE IF NOT EXISTS playlist_song (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    playlist_id BIGINT NOT NULL,
    song_id BIGINT NOT NULL,
    sort_order INT DEFAULT 0,
    FOREIGN KEY (playlist_id) REFERENCES playlist(id) ON DELETE CASCADE,
    FOREIGN KEY (song_id) REFERENCES song(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. User Like
CREATE TABLE IF NOT EXISTS user_like (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    song_id BIGINT NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (song_id) REFERENCES song(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. Play History
CREATE TABLE IF NOT EXISTS play_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    song_id BIGINT NOT NULL,
    play_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (song_id) REFERENCES song(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7. Comment
CREATE TABLE IF NOT EXISTS comment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    song_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (song_id) REFERENCES song(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 8. User Behavior (for collaborative filtering)
CREATE TABLE IF NOT EXISTS user_behavior (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    song_id BIGINT NOT NULL,
    play_count INT DEFAULT 0,
    like_flag TINYINT DEFAULT 0,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (song_id) REFERENCES song(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_song (user_id, song_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 9. Rating
CREATE TABLE IF NOT EXISTS rating (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    song_id BIGINT NOT NULL,
    score DECIMAL(2,1),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (song_id) REFERENCES song(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_song_rating (user_id, song_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 10. Follow
CREATE TABLE IF NOT EXISTS follow (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    follower_id BIGINT NOT NULL,
    followee_id BIGINT NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (follower_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (followee_id) REFERENCES user(id) ON DELETE CASCADE,
    UNIQUE KEY uk_follow (follower_id, followee_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Default admin user (password: admin123, encrypted with BCrypt)
INSERT INTO user (username, password, nickname, role) VALUES
('admin', '$2a$12$pt1/OOe4ciqOXsJJ/qQT0e/tRJtd8Lz9BK1PlgF.5jFSUmWiSM6KC', 'Administrator', 'ADMIN');