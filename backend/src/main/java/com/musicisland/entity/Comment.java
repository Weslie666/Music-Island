package com.musicisland.entity;

public class Comment {
    private Long id;
    private Long userId;
    private Long songId;
    private String content;
    private String username;
    private String songTitle;
    private String createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getSongId() { return songId; }
    public void setSongId(Long songId) { this.songId = songId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getSongTitle() { return songTitle; }
    public void setSongTitle(String songTitle) { this.songTitle = songTitle; }

    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }
}
