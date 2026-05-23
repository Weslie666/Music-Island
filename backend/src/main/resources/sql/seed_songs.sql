-- Music Island Seed Data: 15 Real Songs
-- Durations verified by ffprobe

USE music_island;

DELETE FROM song;

-- ========================
-- 华语流行 (Chinese Pop) - 8 songs
-- ========================
INSERT INTO song (title, artist, album, genre, duration, cover_url, file_url, play_count, like_count) VALUES
('小美满', '周深', '小美满', '华语流行', 214, '/music-data/covers/chinese_01.jpg', '../music-data/audio/chinese_01.mp3', 5800, 1200),
('离别开出花', '就是南方凯', '离别开出花', '华语流行', 230, '/music-data/covers/chinese_02.jpg', '../music-data/audio/chinese_02.mp3', 7200, 1560),
('特别的人', '方大同', '危险世界', '华语流行', 259, '/music-data/covers/chinese_03.jpg', '../music-data/audio/chinese_03.mp3', 3400, 720),
('暮色回响', '张韶涵', '暮色回响', '华语流行', 181, '/music-data/covers/chinese_04.jpg', '../music-data/audio/chinese_04.mp3', 3600, 780),
('平凡之路', '朴树', '平凡之路', '华语流行', 301, '/music-data/covers/chinese_05.jpg', '../music-data/audio/chinese_05.mp3', 8900, 2100),
('无名的人', '毛不易', '无名的人', '华语流行', 282, '/music-data/covers/chinese_06.jpg', '../music-data/audio/chinese_06.mp3', 4900, 1050),
('若月亮没来', '王宇宙 / 乔浚丞', '若月亮没来', '华语流行', 173, '/music-data/covers/chinese_07.jpg', '../music-data/audio/chinese_07.mp3', 4100, 890),
('世界赠予我的', '王菲', '世界赠予我的', '华语流行', 244, '/music-data/covers/chinese_08.jpg', '../music-data/audio/chinese_08.mp3', 5500, 1180);

-- ========================
-- 英语流行 (English Pop) - 5 songs
-- ========================
INSERT INTO song (title, artist, album, genre, duration, cover_url, file_url, play_count, like_count) VALUES
('Wake', 'Hillsong Young & Free', 'We Are Young & Free', '英语流行', 254, '/music-data/covers/english_01.jpg', '../music-data/audio/english_01.mp3', 9200, 2100),
('Love Story', 'Taylor Swift', 'Fearless', '英语流行', 235, '/music-data/covers/english_02.jpg', '../music-data/audio/english_02.mp3', 7800, 1750),
('See You Again', 'Wiz Khalifa ft. Charlie Puth', 'Furious 7 (OST)', '英语流行', 229, '/music-data/covers/english_03.jpg', '../music-data/audio/english_03.mp3', 8500, 1900),
('We Don''t Talk Anymore', 'Charlie Puth ft. Selena Gomez', 'Nine Track Mind', '英语流行', 217, '/music-data/covers/english_04.jpg', '../music-data/audio/english_04.mp3', 6500, 1400),
('Counting Stars', 'OneRepublic', 'Native', '英语流行', 257, '/music-data/covers/english_05.jpg', '../music-data/audio/english_05.mp3', 7100, 1580);

-- ========================
-- 日语流行 (JPOP) - 2 songs
-- ========================
INSERT INTO song (title, artist, album, genre, duration, cover_url, file_url, play_count, like_count) VALUES
('前前前世', 'RADWIMPS', '君の名は。 (OST)', '日语流行', 284, '/music-data/covers/japanese_01.jpg', '../music-data/audio/japanese_01.mp3', 9500, 2200),
('Lemon', '米津玄師', 'Lemon', '日语流行', 256, '/music-data/covers/japanese_02.jpg', '../music-data/audio/japanese_02.mp3', 8800, 1950);
