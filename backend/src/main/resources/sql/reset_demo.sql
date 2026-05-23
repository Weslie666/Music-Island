-- Reset testuser1's behavior data for repeated CF demo
-- Run this before each thesis demonstration

USE music_island;

SET @test1 = (SELECT id FROM user WHERE username = 'testuser1');

-- Remove testuser1's play history and behaviors (keeps seed data intact)
DELETE FROM play_history WHERE user_id = @test1;
DELETE FROM user_behavior WHERE user_id = @test1;
DELETE FROM user_like WHERE user_id = @test1;

-- Re-insert testuser1's seed behaviors (original pattern: 英语流行 + 日语流行)
INSERT INTO user_behavior (user_id, song_id, play_count, like_flag) VALUES
(@test1, 39, 11, 1),  -- Wake - 英语 (与admin重叠)
(@test1, 40,  8, 1),  -- Love Story - 英语 (与admin重叠)
(@test1, 41,  7, 0),  -- See You Again - 英语
(@test1, 43,  6, 1),  -- Counting Stars - 英语
(@test1, 44,  8, 1),  -- 前前前世 - 日语 (与user1重叠)
(@test1, 45,  5, 0),  -- Lemon - 日语 (与user1重叠)
(@test1, 35,  4, 0);  -- 平凡之路 - 华语

-- Note: NO behavior for HEAVEN(46) — it should appear in recommendations
