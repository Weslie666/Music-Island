-- Music Island Seed Data: Test Users + User Behaviors for CF Demo
-- Password for test users: test123

USE music_island;

-- Add test users (skip if already exists)
INSERT IGNORE INTO user (username, password, nickname, role) VALUES
('testuser1', '$2a$12$ccvl8IZWOjdAAEhA/ROcKumfDlpEwPhSqChAjb95cHZIOPXv9HCeq', '音乐爱好者小王', 'USER'),
('testuser2', '$2a$12$ebdnPS5Dl0WEnMPiwpYk3eJOk.jc2LBIIck/etJ1km6QwtiFj1HdW', '乐迷小李', 'USER');

-- Look up actual user IDs (they may vary by environment)
SET @admin = (SELECT id FROM user WHERE username = 'admin');
SET @user1 = (SELECT id FROM user WHERE username = 'user1');
SET @test1 = (SELECT id FROM user WHERE username = 'testuser1');
SET @test2 = (SELECT id FROM user WHERE username = 'testuser2');

-- ============================================================
-- User Behavior Seed Data
-- Song IDs: 31-38 华语流行, 39-43 英语流行, 44-45 日语流行
-- Overlap pattern for CF:
--   admin:     华语+英语
--   user1:     日语+华语  (与admin重叠在华语)
--   testuser1: 英语+日语  (与admin重叠在英语, 与user1重叠在日语)
--   testuser2: 英语+日语  (same pattern, extra data for CF)
-- ============================================================

DELETE FROM user_behavior;

-- Admin: 偏好 华语流行 + 英语流行
INSERT INTO user_behavior (user_id, song_id, play_count, like_flag) VALUES
(@admin, 31, 10, 1),  -- 小美满 - 华语
(@admin, 32,  8, 1),  -- 离别开出花 - 华语
(@admin, 35,  7, 1),  -- 平凡之路 - 华语
(@admin, 36,  6, 0),  -- 无名的人 - 华语
(@admin, 39,  9, 1),  -- Wake - 英语
(@admin, 40,  7, 1),  -- Love Story - 英语
(@admin, 33,  5, 0),  -- 特别的人 - 华语
(@admin, 41,  4, 0);  -- See You Again - 英语

-- user1: 偏好 日语流行 + 华语流行 (与admin重叠在华语)
INSERT INTO user_behavior (user_id, song_id, play_count, like_flag) VALUES
(@user1, 44, 12, 1),  -- 前前前世 - 日语
(@user1, 45, 10, 1),  -- Lemon - 日语
(@user1, 31,  6, 1),  -- 小美满 - 华语 (与admin重叠)
(@user1, 32,  5, 0),  -- 离别开出花 - 华语 (与admin重叠)
(@user1, 34,  4, 1),  -- 暮色回响 - 华语
(@user1, 37,  3, 0),  -- 若月亮没来 - 华语
(@user1, 42,  3, 0);  -- We Don't Talk Anymore - 英语

-- testuser1: 偏好 英语流行 + 日语流行 (与admin重叠英语, 与user1重叠日语)
INSERT INTO user_behavior (user_id, song_id, play_count, like_flag) VALUES
(@test1, 39, 11, 1),  -- Wake - 英语 (与admin重叠)
(@test1, 40,  8, 1),  -- Love Story - 英语 (与admin重叠)
(@test1, 41,  7, 0),  -- See You Again - 英语
(@test1, 43,  6, 1),  -- Counting Stars - 英语
(@test1, 44,  8, 1),  -- 前前前世 - 日语 (与user1重叠)
(@test1, 45,  5, 0),  -- Lemon - 日语 (与user1重叠)
(@test1, 35,  4, 0);  -- 平凡之路 - 华语

-- testuser2: 偏好 英语流行 + 日语流行 (similar to testuser1, 加强CF信号)
INSERT INTO user_behavior (user_id, song_id, play_count, like_flag) VALUES
(@test2, 39, 10, 1),  -- Wake - 英语
(@test2, 43,  9, 1),  -- Counting Stars - 英语
(@test2, 44,  7, 1),  -- 前前前世 - 日语
(@test2, 40,  6, 0),  -- Love Story - 英语
(@test2, 45,  5, 1),  -- Lemon - 日语
(@test2, 42,  4, 0),  -- We Don't Talk Anymore - 英语
(@test2, 31,  3, 0);  -- 小美满 - 华语
