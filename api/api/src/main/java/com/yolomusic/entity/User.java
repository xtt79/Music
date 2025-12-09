package com.yolomusic.entity;

/**
 * 用户实体类
 * 
 * @author YOLOMusic Team
 */
public class User {
    private Integer id;              // 用户ID
    private String username;         // 用户名
    private String password;         // 密码
    private String nickname;         // 用户昵称
    private String role;             // 用户角色：admin-管理员，user-普通用户
    private String status;           // 用户状态：active-活跃，inactive-禁用
    private String createdAt;        // 创建时间
    
    // 构造函数
    public User() {
    }
    
    public User(String username, String password, String nickname, String role, String status) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.role = role;
        this.status = status;
    }
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getNickname() {
        return nickname;
    }
    
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
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
}

