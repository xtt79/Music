package com.yolomusic.entity;

/**
 * 歌单实体类
 * 
 * @author YOLOMusic Team
 */
public class Playlist {
    private Integer id;              // 歌单ID
    private String name;             // 歌单名称
    private String description;       // 歌单描述
    private String coverImage;        // 歌单封面图片URL
    private Integer creatorId;       // 创建者用户ID
    private String creatorName;      // 创建者昵称
    private Integer musicCount;       // 歌单内音乐数量
    private String createdAt;        // 创建时间
    
    // 构造函数
    public Playlist() {
    }
    
    public Playlist(String name, String description, String coverImage, Integer creatorId) {
        this.name = name;
        this.description = description;
        this.coverImage = coverImage;
        this.creatorId = creatorId;
        this.musicCount = 0;
    }
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getCoverImage() {
        return coverImage;
    }
    
    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }
    
    public Integer getCreatorId() {
        return creatorId;
    }
    
    public void setCreatorId(Integer creatorId) {
        this.creatorId = creatorId;
    }
    
    public Integer getMusicCount() {
        return musicCount;
    }
    
    public void setMusicCount(Integer musicCount) {
        this.musicCount = musicCount;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }
}

