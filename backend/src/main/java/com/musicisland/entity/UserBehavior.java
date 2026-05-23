package com.musicisland.entity;

public class UserBehavior {
    private Long id;
    private Long userId;
    private Long songId;
    private Integer playCount;
    private Integer likeFlag;
    private String updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getSongId() { return songId; }
    public void setSongId(Long songId) { this.songId = songId; }

    public Integer getPlayCount() { return playCount; }
    public void setPlayCount(Integer playCount) { this.playCount = playCount; }

    public Integer getLikeFlag() { return likeFlag; }
    public void setLikeFlag(Integer likeFlag) { this.likeFlag = likeFlag; }

    public String getUpdateTime() { return updateTime; }
    public void setUpdateTime(String updateTime) { this.updateTime = updateTime; }
}
