package com.yolomusic.entity;

/**
 * 音乐实体类
 * 
 * @author YOLOMusic Team
 */
public class Music {
    private Integer id;              // 音乐ID
    private String title;            // 歌曲标题
    private String artist;           // 歌手/艺术家
    private String album;            // 专辑名称
    private String genre;            // 音乐流派
    private String fileUrl;           // 音乐文件访问URL
    private String coverImage;       // 封面图片URL
    private Integer playCount;       // 播放次数统计
    private String status;           // 音乐状态：published-已发布，draft-草稿
    private String createdAt;       // 创建时间
    private String addedAt;         // 在歌单中的添加时间（联表查询用）
    
    // 构造函数
    public Music() {
    }
    
    public Music(String title, String artist, String album, String genre, String fileUrl, String coverImage) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.genre = genre;
        this.fileUrl = fileUrl;
        this.coverImage = coverImage;
        this.playCount = 0;
        this.status = "published";
    }
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getArtist() {
        return artist;
    }
    
    public void setArtist(String artist) {
        this.artist = artist;
    }
    
    public String getAlbum() {
        return album;
    }
    
    public void setAlbum(String album) {
        this.album = album;
    }
    
    public String getGenre() {
        return genre;
    }
    
    public void setGenre(String genre) {
        this.genre = genre;
    }
    
    public String getFileUrl() {
        return fileUrl;
    }
    
    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
    
    public String getCoverImage() {
        return coverImage;
    }
    
    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }
    
    public Integer getPlayCount() {
        return playCount;
    }
    
    public void setPlayCount(Integer playCount) {
        this.playCount = playCount;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(String addedAt) {
        this.addedAt = addedAt;
    }
}

